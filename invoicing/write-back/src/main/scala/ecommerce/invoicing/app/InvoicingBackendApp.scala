package ecommerce.invoicing.app

import _root_.akka.cluster.Cluster
import akka.actor.ActorSystem
import akka.kernel.Bootable
import com.typesafe.config.{Config, ConfigFactory}
import ecommerce.invoicing.{Invoice, InvoicingSaga}
import org.slf4j.LoggerFactory._
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.office.Office
import pl.newicom.dddd.office.OfficeFactory.office
import pl.newicom.dddd.process.ReceptorSupport.registerReceptor
import pl.newicom.dddd.process.SagaSupport._
import pl.newicom.dddd.scheduling.{Scheduler, DeadlinesReceptor}

class InvoicingBackendApp extends Bootable with InvoicingBackendConfiguration {

  lazy val log = getLogger(this.getClass.getName)

  val config: Config = ConfigFactory.load()
  implicit lazy val system = ActorSystem("invoicing", config)

  var invoiceOffice: Office[Invoice] = _
  var schedulingOffice: Office[Scheduler] = _

  override def startup() = {
    joinCluster()
    openOffices()
  }

  def openOffices(): Unit = {
    invoiceOffice = office[Invoice]
    schedulingOffice = office[Scheduler]
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