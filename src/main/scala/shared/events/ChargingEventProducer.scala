package shared.events

import nl.vroste.zio.kinesis.client.{Producer, ProducerRecord}
import shared.types.chargingEvent.{ChargingEvent, ChargingEventSerDes}
import zio._
import zio.aws.kinesis.Kinesis

final case class ChargingEventProducer(producer: Producer[ChargingEvent]) {

  def put(event: ChargingEvent): Task[Unit] =
    producer
      .produce(ProducerRecord("123", event))
      .unit
}

object ChargingEventProducer {

  val make: ZLayer[Scope with Any with Kinesis, Throwable, Producer[ChargingEvent]] =
    ZLayer.fromZIO {
      Producer.make("ev-charging_charging-events_stream", ChargingEventSerDes.byteArray)
    }

  val live: ZLayer[Producer[ChargingEvent], Nothing, ChargingEventProducer] =
    ZLayer.fromFunction(ChargingEventProducer.apply _)
}
