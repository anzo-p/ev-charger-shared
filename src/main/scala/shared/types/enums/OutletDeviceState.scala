package shared.types.enums

import enumeratum.{Enum, EnumEntry}
import zio.json.{JsonDecoder, JsonEncoder}
import zio.schema.Schema

sealed trait OutletDeviceState extends EnumEntry

sealed trait AppState

object OutletDeviceState extends Enum[OutletDeviceState] {
  val values: IndexedSeq[OutletDeviceState] = findValues

  implicit val decoder: JsonDecoder[OutletDeviceState] =
    JsonDecoder[String].map(OutletDeviceState.withName)

  implicit val encoder: JsonEncoder[OutletDeviceState] =
    JsonEncoder[String].contramap(_.entryName)

  implicit val schema: Schema[OutletDeviceState] =
    Schema[String].transform(
      OutletDeviceState.withName,
      _.entryName
    )

  case object Available extends OutletDeviceState
  case object CablePlugged extends OutletDeviceState
  case object DeviceRequestsCharging extends OutletDeviceState with AppState
  case object AppRequestsCharging extends OutletDeviceState with AppState
  case object Charging extends OutletDeviceState with AppState
  case object DeviceRequestsStop extends OutletDeviceState with AppState
  case object AppRequestsStop extends OutletDeviceState with AppState
  case object ChargingFinished extends OutletDeviceState with AppState
}
