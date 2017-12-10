package ecommerce.sales.app

import akka.cluster.Cluster
import akka.kernel.Bootable
import ecommerce.sales.ReservationAggregateRoot
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.office.OfficeFactory.office

class SalesBackendApp extends Bootable with SalesBackendConfiguration {

  import commandReception._

  override def startup(): Unit = {
    Cluster(system).registerOnMemberUp {
      office[ReservationAggregateRoot]
      commandReception.receptor
    }
  }

}