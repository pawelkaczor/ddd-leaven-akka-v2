package ecommerce.invoicing.app

import akka.cluster.Cluster
import akka.kernel.Bootable
import ecommerce.invoicing.{Department, Invoice}
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.office.OfficeFactory.office
import pl.newicom.dddd.process.CommandReceptorSupport.{CommandReception, receptor}

class InvoicingBackendApp extends Bootable with InvoicingBackendConfiguration {

  override def startup(): Unit = {
    Cluster(system).registerOnMemberUp {
      office[Invoice]
      CommandReception(Department)(implicit o => receptor)
    }
  }

}