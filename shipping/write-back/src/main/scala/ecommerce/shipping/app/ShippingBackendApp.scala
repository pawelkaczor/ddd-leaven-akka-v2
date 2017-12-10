package ecommerce.shipping.app

import _root_.akka.cluster.Cluster
import akka.kernel.Bootable
import ecommerce.shipping.Department
import ecommerce.shipping.Shipment
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.office.OfficeFactory.office
import pl.newicom.dddd.process.CommandReceptorSupport.{CommandReception, receptor}

class ShippingBackendApp extends Bootable with ShippingBackendConfiguration {

  override def startup(): Unit = {
    Cluster(system).registerOnMemberUp {
      office[Shipment]
      CommandReception(Department)(implicit o => receptor)
    }
  }

}