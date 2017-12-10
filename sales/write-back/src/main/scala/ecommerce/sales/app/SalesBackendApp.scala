package ecommerce.sales.app

import akka.cluster.Cluster
import akka.kernel.Bootable
import ecommerce.sales.Department
import ecommerce.sales.ReservationAggregateRoot
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.office.OfficeFactory.office
import pl.newicom.dddd.process.CommandReceptorSupport.{CommandReception, receptor}

class SalesBackendApp extends Bootable with SalesBackendConfiguration {

  override def startup(): Unit = {
    Cluster(system).registerOnMemberUp {
      office[ReservationAggregateRoot]
      CommandReception(Department)(implicit o => receptor)
    }
  }

}