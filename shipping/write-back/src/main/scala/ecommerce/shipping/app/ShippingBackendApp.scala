package ecommerce.shipping.app

import _root_.akka.cluster.Cluster
import akka.kernel.Bootable
import ecommerce.shipping.Shipment
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.office.OfficeFactory.office

class ShippingBackendApp extends Bootable with ShippingBackendConfiguration {

  import commandReception._

  override def startup(): Unit = {
    Cluster(system).registerOnMemberUp {
      office[Shipment]
      commandReception.receptor
    }
  }

}