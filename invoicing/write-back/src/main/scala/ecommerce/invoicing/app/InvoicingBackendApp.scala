package ecommerce.invoicing.app

import _root_.akka.cluster.Cluster
import akka.kernel.Bootable
import ecommerce.invoicing.{Invoice, InvoicingSaga}
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.office.OfficeFactory.office
import pl.newicom.dddd.process.ReceptorSupport.registerReceptor
import pl.newicom.dddd.scheduling.{DeadlinesReceptor, Scheduler}

class InvoicingBackendApp extends Bootable with InvoicingBackendConfiguration {

  override def startup() = {
    Cluster(system).registerOnMemberUp {
      office[Invoice]
      office[Scheduler]
      office[InvoicingSaga]
      registerReceptor(DeadlinesReceptor("global"))
    }
  }

}