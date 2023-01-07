package shared.events.kinesis

import nl.vroste.zio.kinesis.client.serde.Serde
import nl.vroste.zio.kinesis.client.{Producer, ProducerRecord, Record}
import shared.events.DeadLetterProducer
import shared.types.deadLetters.DeadLetterMessage
import zio.aws.kinesis.Kinesis
import zio.{Scope, Task, ZLayer}

final case class KinesisDeadLetters(producer: Producer[String]) extends DeadLetterProducer {
  import zio.json._

  private def put(message: String): Task[Unit] =
    producer
      .produce(ProducerRecord("123", message))
      .unit

  def send[T](rec: Record[T], th: Throwable): Task[Unit] =
    put(DeadLetterMessage.make[T](rec, th).toJson)
}

object KinesisDeadLetters {

  val make: ZLayer[Scope with Any with Kinesis, Throwable, Producer[String]] =
    ZLayer.fromZIO {
      Producer.make("ev-charging_charging-events-dead-letters_stream", Serde.asciiString)
    }

  val live: ZLayer[Producer[String], Nothing, DeadLetterProducer] =
    ZLayer.fromFunction(KinesisDeadLetters.apply _)
}
