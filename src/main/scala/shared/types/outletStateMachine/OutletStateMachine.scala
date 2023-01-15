package shared.types.outletStateMachine

import shared.types.enums.OutletDeviceState
import shared.types.enums.OutletDeviceState._

import scala.collection.mutable

trait OutletStateMachine {

  def outletState: OutletDeviceState
}

object OutletStateMachine {
  private object Transitions {

    val allowedTransitions: Map[OutletDeviceState, Seq[OutletDeviceState]] =
      Map(
        Available              -> Seq(CablePlugged),
        CablePlugged           -> Seq(Available, DeviceRequestsCharging, AppRequestsCharging, Charging),
        DeviceRequestsCharging -> Seq(Available, CablePlugged, Charging),
        AppRequestsCharging    -> Seq(Available, CablePlugged, Charging),
        Charging               -> Seq(Charging, DeviceRequestsStop, AppRequestsStop, ChargingFinished),
        DeviceRequestsStop     -> Seq(ChargingFinished),
        AppRequestsStop        -> Seq(ChargingFinished),
        ChargingFinished       -> Seq(CablePlugged)
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

    def getPreStatesTo(next: OutletDeviceState): Seq[OutletDeviceState] =
      Transitions.preStates(next)
  }

  implicit class Ops(entity: OutletStateMachine) {

    def mayTransitionTo(targetState: OutletDeviceState): Boolean =
      entity.outletState.in(Transitions.getPreStatesTo(targetState))
  }

  def cannotTransitionTo(targetState: OutletDeviceState): String = {
    val preStates = Transitions
      .getPreStatesTo(targetState)
      .sortBy(_.entryName)
      .mkString("{ ", ", ", " }")

    s"outlet state must be in one of $preStates in order to transition into $targetState"
  }
}
