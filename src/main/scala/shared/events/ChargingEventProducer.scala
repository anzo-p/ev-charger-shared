package shared.events

import shared.types.chargingEvent.ChargingEvent
import zio.Task

trait ChargingEventProducer {
  def put(event: ChargingEvent): Task[Unit]
}
