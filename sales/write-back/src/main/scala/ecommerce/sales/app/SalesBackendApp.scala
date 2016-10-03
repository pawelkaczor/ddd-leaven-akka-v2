package ecommerce.sales.app

import akka.cluster.Cluster
import akka.kernel.Bootable
import ecommerce.sales.{OrderSaga, Reservation}
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.office.OfficeFactory.office

class SalesBackendApp extends Bootable with SalesBackendConfiguration {

  override def startup() = {
    Cluster(system).registerOnMemberUp {
      office[Reservation]
      office[OrderSaga]
    }
  }

}