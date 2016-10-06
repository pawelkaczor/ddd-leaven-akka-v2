package ecommerce.shipping.app

import _root_.akka.cluster.Cluster
import akka.kernel.Bootable
import ecommerce.shipping.Shipment
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.office.OfficeFactory.office

class ShippingBackendApp extends Bootable with ShippingBackendConfiguration {

  override def startup() = {
    Cluster(system).registerOnMemberUp {
      office[Shipment]
    }
  }

}