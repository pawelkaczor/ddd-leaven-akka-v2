package ecommerce.headquarters.app

import akka.cluster.Cluster
import akka.cluster.http.management.ClusterHttpManagement
import akka.kernel.Bootable
import ecommerce.headquarters.processes.OrderProcessManager
import ecommerce.invoicing.InvoicingOfficeId
import ecommerce.sales.ReservationOfficeId
import ecommerce.shipping.ShippingOfficeId
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.office.OfficeFactory._
import pl.newicom.dddd.office.OfficeRegistry
import pl.newicom.dddd.process.CommandSink
import pl.newicom.dddd.scheduling.Scheduler

class HeadquartersApp extends Bootable with HeadquartersConfiguration {

  lazy val offices = OfficeRegistry(system)

  override def startup(): Unit = {
    val cluster = Cluster(system)

    cluster.registerOnMemberUp {
      office[Scheduler]
      office[OrderProcessManager]
      office[CommandSink]

      List(InvoicingOfficeId, ReservationOfficeId, ShippingOfficeId).foreach {
        offices.registerOffice(_, external = true)
      }
    }

    ClusterHttpManagement(cluster).start()
  }

}
