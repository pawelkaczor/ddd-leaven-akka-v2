package ecommerce.headquarters.app

import akka.cluster.Cluster
import akka.kernel.Bootable
import ecommerce.headquarters.ClusterView
import ecommerce.headquarters.processes.OrderProcessManager
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.office.OfficeFactory._
import pl.newicom.dddd.scheduling.Scheduler

class HeadquartersApp extends Bootable with HeadquartersConfiguration {

  override def startup(): Unit = {
    system.actorOf(ClusterView.props, ClusterView.Name)

    Cluster(system).registerOnMemberUp {
      office[Scheduler]
      office[OrderProcessManager]
    }
  }

}
