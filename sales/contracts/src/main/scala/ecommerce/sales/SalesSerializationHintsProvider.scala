package ecommerce.sales

import org.json4s.ext.EnumNameSerializer
import org.json4s.{Formats, NoTypeHints}
import pl.newicom.dddd.serialization.{JsonExtraSerHints, JsonSerializationHintsProvider}

class SalesSerializationHintsProvider extends JsonSerializationHintsProvider {

  val serializers = List(new EnumNameSerializer(ProductType))

  override def hints(default: Formats) = JsonExtraSerHints(NoTypeHints, serializers)
}
