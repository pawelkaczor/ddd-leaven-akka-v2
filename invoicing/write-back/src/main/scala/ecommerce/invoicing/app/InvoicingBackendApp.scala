package ecommerce.invoicing.app

import _root_.akka.cluster.Cluster
import akka.actor.{ActorRef, ActorSystem}
import akka.kernel.Bootable
import com.typesafe.config.{Config, ConfigFactory}
import ecommerce.invoicing.{Invoice, InvoicingSaga}
import org.slf4j.LoggerFactory._
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.office.Office._
import pl.newicom.dddd.process.ReceptorSupport.registerReceptor
import pl.newicom.dddd.process.SagaSupport._
import pl.newicom.dddd.scheduling.{Scheduler, DeadlinesReceptor}

class InvoicingBackendApp extends Bootable with InvoicingBackendConfiguration {

  lazy val log = getLogger(this.getClass.getName)

  val config: Config = ConfigFactory.load()
  implicit val system = ActorSystem("invoicing", config)

  var _invoiceOffice: ActorRef = null
  var _schedulingOffice: ActorRef = null
  def invoiceOffice = _invoiceOffice.path
  def schedulingOffice = _schedulingOffice.path

  override def startup() = {
    joinCluster()
    openOffices()
  }

  def openOffices(): Unit = {
    _invoiceOffice = office[Invoice]
    _schedulingOffice = office[Scheduler]
    registerSaga[InvoicingSaga]
    registerReceptor(DeadlinesReceptor("global"))
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