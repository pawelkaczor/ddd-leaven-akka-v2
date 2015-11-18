package ecommerce.shipping.app

import _root_.akka.cluster.Cluster
import akka.actor.ActorSystem
import akka.kernel.Bootable
import com.typesafe.config.{Config, ConfigFactory}
import ecommerce.shipping.{PaymentReceptor, Shipment}
import org.slf4j.LoggerFactory._
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.office.OfficeFactory.office
import pl.newicom.dddd.process.ReceptorSupport._

class ShippingBackendApp extends Bootable with ShippingBackendConfiguration {

  lazy val log = getLogger(this.getClass.getName)

  val config: Config = ConfigFactory.load()
  implicit val system = ActorSystem("shipping", config)

  override def startup() = {
    joinCluster()
    openOffices()
  }

  def openOffices(): Unit = {
    val shippingOffice = office[Shipment]
    registerReceptor(PaymentReceptor(shippingOffice.actorPath))
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