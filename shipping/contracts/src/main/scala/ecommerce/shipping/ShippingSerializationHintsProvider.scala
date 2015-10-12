package ecommerce.shipping

import org.json4s.ext.EnumSerializer
import org.json4s.{Formats, NoTypeHints}
import pl.newicom.dddd.serialization.{JsonExtraSerHints, JsonSerializationHintsProvider}

class ShippingSerializationHintsProvider extends JsonSerializationHintsProvider {

  val serializers = List(new EnumSerializer(ShippingStatus))

  override def hints(default: Formats) = JsonExtraSerHints(NoTypeHints, serializers)
}
