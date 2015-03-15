package ecommerce.shipping.eventstore

import akka.actor.ExtendedActorSystem
import ecommerce.shipping.shippingOffice
import org.json4s.Formats
import pl.newicom.eventstore.Json4sEsSerializer

class EventStoreSerializer(val sys: ExtendedActorSystem) extends Json4sEsSerializer(sys) {

  override implicit val formats: Formats = shippingOffice.serializationHints ++ defaultFormats

}
