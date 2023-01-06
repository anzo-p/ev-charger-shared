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
  case object Broken extends OutletDeviceState
  case object CablePlugged extends OutletDeviceState
  case object CableRemoved extends OutletDeviceState
  case object Cancelled extends OutletDeviceState
  case object ChargingRequested extends OutletDeviceState with AppState
  case object Charging extends OutletDeviceState with AppState
  case object StoppingRequested extends OutletDeviceState with AppState
  case object Finished extends OutletDeviceState with AppState

  private object Transitions {

    val allowedTransitions: Map[OutletDeviceState, Seq[OutletDeviceState]] =
      Map(
        Available         -> Seq(CablePlugged),
        CablePlugged      -> Seq(ChargingRequested, Charging, CableRemoved),
        ChargingRequested -> Seq(Charging, CableRemoved),
        Charging          -> Seq(Charging, StoppingRequested, Finished, CableRemoved),
        StoppingRequested -> Seq(Finished, CableRemoved),
        Finished          -> Seq(CableRemoved),
        CableRemoved      -> Seq(Available)
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
