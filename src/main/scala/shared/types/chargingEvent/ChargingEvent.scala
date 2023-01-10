package shared.types.chargingEvent

import shared.types.enums.{EventInitiator, OutletDeviceState}
import shared.types.outletStateMachine.OutletStateMachine

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
  ) extends OutletStateMachine

object ChargingEvent {

  def deviceStart(outletId: UUID, rfidTag: String): ChargingEvent =
    ChargingEvent(
      initiator   = EventInitiator.OutletBackend,
      outletId    = outletId,
      outletState = OutletDeviceState.DeviceRequestsCharging,
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
      initiator   = EventInitiator.OutletBackend,
      outletId    = outletId,
      outletState = OutletDeviceState.DeviceRequestsStop,
      recentSession = EventSession(
        sessionId        = None,
        rfidTag          = rfidTag,
        periodStart      = java.time.OffsetDateTime.now(),
        periodEnd        = Some(java.time.OffsetDateTime.now()),
        powerConsumption = 0.0
      )
    )
}
