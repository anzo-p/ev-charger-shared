package shared.types.enums

import enumeratum.{Enum, EnumEntry}
import zio.json.{DeriveJsonCodec, JsonCodec}

sealed trait EventInitiator extends EnumEntry

object EventInitiator extends Enum[EventInitiator] {
  val values: IndexedSeq[EventInitiator] = findValues

  implicit val codec: JsonCodec[EventInitiator] =
    DeriveJsonCodec.gen[EventInitiator]

  case object Application extends EventInitiator
  case object OutletDevice extends EventInitiator
}
