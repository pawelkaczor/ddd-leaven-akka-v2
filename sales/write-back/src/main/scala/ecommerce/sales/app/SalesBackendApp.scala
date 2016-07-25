package ecommerce.sales.app

import akka.cluster.Cluster
import akka.actor.ActorSystem
import akka.kernel.Bootable
import com.typesafe.config.{Config, ConfigFactory}
import ecommerce.sales.{OrderSaga, Reservation}
import org.slf4j.LoggerFactory._
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.office.{Office, OfficeFactory}
import pl.newicom.dddd.process.SagaSupport.registerSaga

class SalesBackendApp extends Bootable with SalesBackendConfiguration {

  lazy val log = getLogger(getClass.getName)

  lazy val config: Config = ConfigFactory.load()
  implicit lazy val system = ActorSystem("sales", config)

  var reservationOffice: Office[Reservation] = _

  override def startup() = {
    joinCluster()
    openOffices()
  }

  def openOffices(): Unit = {
    reservationOffice = OfficeFactory.office[Reservation]
    registerSaga[OrderSaga]
  }

  def joinCluster(): Unit = {
    val seedList = seeds(config)
    log.info(s"Joining cluster with seed nodes: $seedList")
    Cluster(system).joinSeedNodes(seedList)
  }

 override def shutdown() = {
   system.terminate()
  }

}