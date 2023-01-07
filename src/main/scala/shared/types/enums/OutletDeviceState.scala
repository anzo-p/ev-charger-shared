package shared.types.enums

import enumeratum.{Enum, EnumEntry}
import zio.json.{JsonDecoder, JsonEncoder}
import zio.schema.Schema

import scala.collection.mutable

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

  private object Transitions {

    val allowedTransitions: Map[OutletDeviceState, Seq[OutletDeviceState]] =
      Map(
        Available              -> Seq(CablePlugged),
        CablePlugged           -> Seq(Available, DeviceRequestsCharging, AppRequestsCharging),
        DeviceRequestsCharging -> Seq(Available, CablePlugged, Charging),
        AppRequestsCharging    -> Seq(Available, CablePlugged, Charging),
        Charging               -> Seq(Charging, DeviceRequestsStop, AppRequestsStop),
        DeviceRequestsStop     -> Seq(CablePlugged),
        AppRequestsStop        -> Seq(CablePlugged)
      )

    val preStates: mutable.HashMap[OutletDeviceState, Seq[OutletDeviceState]] = mutable.HashMap()

    allowedTransitions.keys.foreach { key =>
      allowedTransitions.keys.foreach { value =>
        if (allowedTransitions(key).contains(value)) {
          if (preStates.contains(value)) {
            preStates.put(value, preStates(value) :+ key)
          }
          else {
            preStates.put(value, Seq(key))
          }
        }
      }
    }
  }

  def getPreStatesTo(next: OutletDeviceState): Seq[OutletDeviceState] =
    OutletDeviceState.Transitions.preStates(next)

  def cannotTransitionTo(targetState: OutletDeviceState): String =
    s"outlet not in (one of) state(s) ${OutletDeviceState.getPreStatesTo(targetState).mkString("[ ", " ,", " ]")}"
}
