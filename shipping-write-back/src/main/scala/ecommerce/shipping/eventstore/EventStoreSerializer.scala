package ecommerce.shipping.eventstore

import akka.actor.ExtendedActorSystem
import ecommerce.shipping
import ecommerce.sales.{formats, typeHints}
import org.json4s.Formats
import pl.newicom.eventstore.Json4sEsSerializer

class EventStoreSerializer(val sys: ExtendedActorSystem) extends Json4sEsSerializer(sys) {

  override implicit val formats: Formats = defaultFormats + shipping.typeHints + shipping.formats

}
