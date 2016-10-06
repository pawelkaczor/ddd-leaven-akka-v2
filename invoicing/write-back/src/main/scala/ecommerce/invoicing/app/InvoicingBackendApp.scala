package ecommerce.invoicing.app

import akka.cluster.Cluster
import akka.kernel.Bootable
import ecommerce.invoicing.Invoice
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.office.OfficeFactory.office

class InvoicingBackendApp extends Bootable with InvoicingBackendConfiguration {

  override def startup() = {
    Cluster(system).registerOnMemberUp {
      office[Invoice]
    }
  }

}