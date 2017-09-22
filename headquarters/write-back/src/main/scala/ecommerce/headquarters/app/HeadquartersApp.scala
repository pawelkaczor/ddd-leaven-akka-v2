package ecommerce.headquarters.app

import akka.cluster.Cluster
import akka.cluster.http.management.ClusterHttpManagement
import akka.kernel.Bootable
import ecommerce.headquarters.processes.OrderProcessManager
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.office.OfficeFactory._
import pl.newicom.dddd.scheduling.Scheduler

class HeadquartersApp extends Bootable with HeadquartersConfiguration {

  override def startup(): Unit = {
    val cluster = Cluster(system)
    cluster.registerOnMemberUp {
      office[Scheduler]
      office[OrderProcessManager]
    }
    ClusterHttpManagement(cluster).start()
  }

}
