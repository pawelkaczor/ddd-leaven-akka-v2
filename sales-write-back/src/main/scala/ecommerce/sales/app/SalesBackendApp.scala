package ecommerce.sales.app

import _root_.akka.cluster.Cluster
import akka.actor.ActorSystem
import akka.kernel.Bootable
import com.typesafe.config.{Config, ConfigFactory}
import ecommerce.sales.Reservation
import org.slf4j.LoggerFactory._
import pl.newicom.dddd.office.Office._
import pl.newicom.dddd.cluster._

class SalesBackendApp extends Bootable with SalesBackendConfiguration {

  lazy val log = getLogger(this.getClass.getName)

  val config: Config = ConfigFactory.load()
  implicit val system = ActorSystem("sales", config)

  override def startup() = {
    joinCluster()
    openOffices()
  }

  def openOffices(): Unit = {
    office[Reservation]
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