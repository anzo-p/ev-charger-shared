package shared.types.chargingEvent

import com.anzop.evCharger.chargingEvent._
import nl.vroste.zio.kinesis.client.serde.Serde
import nl.vroste.zio.kinesis.client.serde.Serde.bytes
import shared.types.enums.{EventInitiator, OutletDeviceState}
import shared.types.TimeExtensions._
import zio.Chunk

import java.util.UUID

object ChargingEventSerDes {

  def toProtobuf(session: EventSession): EventSessionProto =
    EventSessionProto(
      sessionId        = session.sessionId.getOrElse("").toString,
      rfidTag          = session.rfidTag,
      periodStart      = Some(session.periodStart.toProtobufTs),
      periodEnd        = session.periodEnd.map(_.toProtobufTs),
      powerConsumption = session.powerConsumption
    )

  def toProtobuf(outlet: ChargingEvent): ChargingEventProto =
    ChargingEventProto(
      initiator     = outlet.initiator.entryName,
      outletId      = outlet.outletId.toString,
      outletState   = outlet.outletState.entryName,
      recentSession = Some(toProtobuf(outlet.recentSession))
    )

  def fromProtobuf(proto: EventSessionProto): EventSession =
    EventSession(
      sessionId        = if (proto.sessionId == "") None else Some(UUID.fromString(proto.sessionId)),
      rfidTag          = proto.rfidTag,
      periodStart      = proto.periodStart.map(_.toJavaOffsetDateTime).get, // scalapb makes it an option
      periodEnd        = proto.periodEnd.map(_.toJavaOffsetDateTime),
      powerConsumption = proto.powerConsumption
    )

  def fromProtobuf(proto: ChargingEventProto): ChargingEvent =
    ChargingEvent(
      initiator     = EventInitiator.withName(proto.initiator),
      outletId      = UUID.fromString(proto.outletId),
      outletState   = OutletDeviceState.withName(proto.outletState),
      recentSession = fromProtobuf(proto.recentSession.get)
    )

  val byteArray: Serde[Any, ChargingEvent] =
    bytes.inmap(chunk => fromProtobuf(ChargingEventProto.parseFrom(chunk.toArray)))(event => Chunk.fromArray(toProtobuf(event).toByteArray))
}
