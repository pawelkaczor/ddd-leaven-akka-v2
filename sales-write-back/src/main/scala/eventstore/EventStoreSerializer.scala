package eventstore

import ecommerce.sales
import org.json4s.Formats
import pl.newicom.eventstore.Json4sEsSerializer

class EventStoreSerializer extends Json4sEsSerializer {

  override implicit val formats: Formats = defaultFormats + sales.typeHints

}
