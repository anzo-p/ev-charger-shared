package shared.events

import nl.vroste.zio.kinesis.client.Record
import nl.vroste.zio.kinesis.client.zionative.{Consumer, LeaseRepository}
import shared.types.enums.EventInitiator
import shared.types.chargingEvent.{ChargingEvent, ChargingEventSerDes}
import zio.Console.printLine
import zio.aws.kinesis.Kinesis
import zio.{durationInt, Task, ZIO}

trait ChargingEventConsumer {

  val resourceName = "ev-charging_charging-events_stream"

  def applicationName: String

  def follow: EventInitiator

  def deadLetters: DeadLetterProducer

  def consume(data: ChargingEvent): Task[Unit]

  private def consumeAndCatch(record: Record[ChargingEvent]): ZIO[Any, Throwable, Unit] =
    consume(record.data).catchAll {
      case th: Throwable => deadLetters.send[ChargingEvent](record, th)
      case _             => ZIO.succeed(())
    }

  def start: ZIO[Kinesis with LeaseRepository with Any, Throwable, Unit] =
    Consumer
      .shardedStream(
        streamName      = resourceName,
        applicationName = s"ev-charging_charging-event-checkpoints-${applicationName}_table",
        deserializer    = ChargingEventSerDes.byteArray
      )
      .flatMapPar(4) {
        case (shardId, shardStream, checkpointer) =>
          shardStream
            .filter(_.data.initiator == follow)
            .tap(record => printLine(s"Processing record $record on shard $shardId"))
            .tap(_ => ZIO.succeed(Thread.sleep(1111))) // slow down for now, later find out why required
            .tap(consumeAndCatch)
            .tap(checkpointer.stage(_))
            .viaFunction(checkpointer.checkpointBatched[Any](nr = 1000, interval = 5.minutes))
      }
      .tap(_ => ZIO.succeed(Thread.sleep(1111))) // slow down for now, later find out why required
      .runDrain
}
