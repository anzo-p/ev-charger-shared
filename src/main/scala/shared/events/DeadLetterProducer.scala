package shared.events

import nl.vroste.zio.kinesis.client.Record
import zio.Task

trait DeadLetterProducer {
  def send[T](rec: Record[T], th: Throwable): Task[Unit]
}
