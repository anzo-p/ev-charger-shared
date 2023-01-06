package shared.types.chargingEvent

import shared.types.enums.{EventInitiator, OutletDeviceState}

import java.util.UUID

final case class EventSession(
    sessionId: Option[UUID],
    rfidTag: String,
    periodStart: java.time.OffsetDateTime,
    periodEnd: Option[java.time.OffsetDateTime],
    powerConsumption: Double
  )

final case class ChargingEvent(
    initiator: EventInitiator,
    outletId: UUID,
    outletState: OutletDeviceState,
    recentSession: EventSession
  )

object ChargingEvent {

  def deviceStart(outletId: UUID, rfidTag: String): ChargingEvent =
    ChargingEvent(
      initiator   = EventInitiator.OutletDevice,
      outletId    = outletId,
      outletState = OutletDeviceState.ChargingRequested,
      recentSession = EventSession(
        sessionId        = None,
        rfidTag          = rfidTag,
        periodStart      = java.time.OffsetDateTime.now(),
        periodEnd        = None,
        powerConsumption = 0.0
      )
    )

  def deviceStop(outletId: UUID, rfidTag: String): ChargingEvent =
    ChargingEvent(
      initiator   = EventInitiator.OutletDevice,
      outletId    = outletId,
      outletState = OutletDeviceState.Finished,
      recentSession = EventSession(
        sessionId        = None,
        rfidTag          = rfidTag,
        periodStart      = java.time.OffsetDateTime.now(),
        periodEnd        = Some(java.time.OffsetDateTime.now()),
        powerConsumption = 0.0
      )
    )
}
