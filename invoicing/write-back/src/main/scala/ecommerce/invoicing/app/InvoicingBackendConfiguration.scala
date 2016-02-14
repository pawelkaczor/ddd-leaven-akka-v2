package ecommerce.invoicing.app

import java.net.InetAddress

import akka.actor._
import com.typesafe.config.Config
import ecommerce.invoicing.{Invoice, InvoicingSaga}
import eventstore.EsConnection
import org.slf4j.Logger
import pl.newicom.dddd.actor.{CreationSupport, PassivationConfig}
import pl.newicom.dddd.aggregate.AggregateRootActorFactory
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.eventhandling.NoPublishing
import pl.newicom.dddd.monitoring.{ReceptorMonitoring, SagaMonitoring, AggregateRootMonitoring}
import pl.newicom.dddd.office.Office
import pl.newicom.dddd.process.ReceptorSupport.ReceptorFactory
import pl.newicom.dddd.process.SagaSupport._
import pl.newicom.dddd.process.{Saga, Receptor, SagaActorFactory, SagaManager}
import pl.newicom.dddd.scheduling.Scheduler
import pl.newicom.eventstore.EventstoreSubscriber

import scala.concurrent.duration.{DurationInt, Duration}
import scala.io.Source
import scala.util.Try

trait InvoicingBackendConfiguration {

  def log: Logger
  def config: Config
  implicit def system: ActorSystem
  def creationSupport = implicitly[CreationSupport]

  def invoiceOffice: Office[Invoice]
  def schedulingOffice: Office[Scheduler]


  //
  // Scheduling
  //
  implicit object SchedulerFactory extends AggregateRootActorFactory[Scheduler] {
    override def props(pc: PassivationConfig) = Props(new Scheduler(pc) with NoPublishing with AggregateRootMonitoring {
      override def id = "global"
    })
  }
  implicit object SchedulerShardResolution extends DefaultShardResolution[Scheduler]

  //
  // Invoicing
  //
  implicit object InvoiceARFactory extends AggregateRootActorFactory[Invoice] {
    override def props(pc: PassivationConfig) = Props(new Invoice(pc) with NoPublishing)
  }
  implicit object InvoiceShardResolution extends DefaultShardResolution[Invoice]
  implicit object InvoicingSagaShardResolution extends DefaultShardResolution[InvoicingSaga]

  implicit object InvoicingSagaActorFactory extends SagaActorFactory[InvoicingSaga] {
    // avoid Saga passivation during a stress-test as it results in a delivery failure of DeliveryTick (ALOD) messages
    override def inactivityTimeout: Duration = 30 minutes

    def props(pc: PassivationConfig): Props = {
      Props(new InvoicingSaga(pc, invoiceOffice.actorPath, Some(schedulingOffice.actorPath)) with SagaMonitoring)
    }
  }

  implicit def sagaManagerFactory[E <: Saga]: SagaManagerFactory[E] = sagaOffice => {
    new SagaManager()(sagaOffice) with EventstoreSubscriber with ReceptorMonitoring[EsConnection]
  }

  implicit val receptorFactory: ReceptorFactory = receptorConfig => {
    new Receptor with EventstoreSubscriber with ReceptorMonitoring[EsConnection] {
      def config = receptorConfig
    }
  }

  def seeds(config: Config) = {
    // Read cluster seed nodes from the file specified in the configuration
    Try(config.getString("app.cluster.seedsFile")).toOption match {
      case Some(seedsFile) =>
        // Seed file was specified, read it
        log.info(s"reading seed nodes from file: $seedsFile")
        Source.fromFile(seedsFile).getLines().map { address =>
          AddressFromURIString.parse(s"akka.tcp://invoicing@$address")
        }.toList
      case None =>
        // No seed file specified, use this node as the first seed
        log.info("no seed file found, using default seeds")
        val port = config.getInt("app.port")
        val localAddress = Try(config.getString("app.host"))
          .toOption.getOrElse(InetAddress.getLocalHost.getHostAddress)
        List(AddressFromURIString.parse(s"akka.tcp://invoicing@$localAddress:$port"))
    }
  }

}
