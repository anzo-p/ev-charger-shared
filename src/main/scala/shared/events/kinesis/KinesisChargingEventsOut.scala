package shared.events.kinesis

import nl.vroste.zio.kinesis.client.{Producer, ProducerRecord}
import shared.events.ChargingEventProducer
import shared.types.chargingEvent.{ChargingEvent, ChargingEventSerDes}
import zio.aws.kinesis.Kinesis
import zio.{Scope, Task, ZLayer}

import java.util.UUID

final case class KinesisChargingEventsOut(producer: Producer[ChargingEvent]) extends ChargingEventProducer {

  def put(event: ChargingEvent): Task[Unit] =
    producer
      .produce(ProducerRecord(UUID.randomUUID().toString, event))
      .unit
}

object KinesisChargingEventsOut {

  val make: ZLayer[Scope with Any with Kinesis, Throwable, Producer[ChargingEvent]] =
    ZLayer.fromZIO {
      Producer.make("ev-charging_charging-events_stream", ChargingEventSerDes.byteArray)
    }

  val live: ZLayer[Producer[ChargingEvent], Nothing, ChargingEventProducer] =
    ZLayer.fromFunction(KinesisChargingEventsOut.apply _)
}
