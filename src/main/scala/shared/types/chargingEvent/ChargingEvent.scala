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
