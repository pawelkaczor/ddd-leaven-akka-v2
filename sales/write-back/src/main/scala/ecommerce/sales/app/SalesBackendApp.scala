package ecommerce.sales.app

import _root_.akka.cluster.Cluster
import akka.actor.{ActorRef, ActorSystem}
import akka.kernel.Bootable
import com.typesafe.config.{Config, ConfigFactory}
import ecommerce.sales.{OrderSaga, Reservation}
import org.slf4j.LoggerFactory._
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.office.{Office, OfficeFactory}
import pl.newicom.dddd.process.SagaSupport.registerSaga

class SalesBackendApp extends Bootable with SalesBackendConfiguration {

  lazy val log = getLogger(this.getClass.getName)

  val config: Config = ConfigFactory.load()
  implicit val system = ActorSystem("sales", config)

  var reservationOffice: Office[Reservation] = null

  override def startup() = {
    joinCluster()
    openOffices()
  }

  def openOffices(): Unit = {
    reservationOffice = OfficeFactory.office[Reservation]
    registerSaga[OrderSaga]
  }

  /**
   * Join the cluster with the specified seed nodes and block until termination
   */
  def joinCluster(): Unit = {
    val seedList = seeds(config)
    log.info(s"Joining cluster with seed nodes: $seedList")
    Cluster(system).joinSeedNodes(seedList.toSeq)
  }

 override def shutdown() = {
    system.terminate()
  }

}