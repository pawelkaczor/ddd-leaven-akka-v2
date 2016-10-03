package ecommerce.shipping.app

import _root_.akka.cluster.Cluster
import akka.kernel.Bootable
import ecommerce.shipping.{PaymentReceptor, Shipment}
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.office.OfficeFactory.office
import pl.newicom.dddd.process.ReceptorSupport._

class ShippingBackendApp extends Bootable with ShippingBackendConfiguration {

  override def startup() = {
    Cluster(system).registerOnMemberUp {
      val shippingOffice = office[Shipment]
      registerReceptor(PaymentReceptor(shippingOffice.actorPath))
    }
  }

}