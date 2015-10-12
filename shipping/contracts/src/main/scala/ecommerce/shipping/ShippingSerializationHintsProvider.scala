package ecommerce.shipping

import org.json4s.ext.EnumSerializer
import org.json4s.{Formats, FullTypeHints}
import pl.newicom.dddd.serialization.{JsonExtraSerHints, JsonSerializationHintsProvider}

class ShippingSerializationHintsProvider extends JsonSerializationHintsProvider {

  val typeHints = ShippingCommands + ShippingEvents
  val serializers = List(new EnumSerializer(ShippingStatus))

  object ShippingCommands extends FullTypeHints(
    List(
      classOf[CreateShipment]
    ))

  object ShippingEvents extends FullTypeHints(
    List(
      classOf[ShipmentCreated]
    ))

  override def hints(default: Formats) = JsonExtraSerHints(typeHints, serializers)
}
