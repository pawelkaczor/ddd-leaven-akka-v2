package ecommerce.invoicing.app

import java.net.InetAddress

import akka.actor._
import com.typesafe.config.Config
import ecommerce.invoicing.{Invoice, InvoicingSaga}
import org.slf4j.Logger
import pl.newicom.dddd.actor.{CreationSupport, PassivationConfig}
import pl.newicom.dddd.aggregate.AggregateRootActorFactory
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.eventhandling.EventPublisher
import pl.newicom.dddd.messaging.event.OfficeEventMessage
import pl.newicom.dddd.office.Office
import pl.newicom.dddd.persistence.PersistentActorLogging
import pl.newicom.dddd.process.ReceptorSupport.ReceptorFactory
import pl.newicom.dddd.process.SagaSupport._
import pl.newicom.dddd.process.{Saga, Receptor, SagaActorFactory, SagaManager}
import pl.newicom.dddd.scheduling.Scheduler
import pl.newicom.eventstore.EventstoreSubscriber

import scala.io.Source
import scala.util.Try

trait LocalPublisher extends EventPublisher {
  this: Actor with PersistentActorLogging =>

  override def publish(em: OfficeEventMessage): Unit = {
    context.system.eventStream.publish(em.event)
    log.debug(s"Published: $em")
  }
}

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
    override def props(pc: PassivationConfig) = Props(new Scheduler(pc) with LocalPublisher {
      override def id = "global"
    })
  }
  implicit object SchedulerShardResolution extends DefaultShardResolution[Scheduler]

  //
  // Invoicing
  //
  implicit object InvoiceARFactory extends AggregateRootActorFactory[Invoice] {
    override def props(pc: PassivationConfig) = Props(new Invoice(pc) with LocalPublisher)
  }
  implicit object InvoiceShardResolution extends DefaultShardResolution[Invoice]
  implicit object InvoicingSagaShardResolution extends DefaultShardResolution[InvoicingSaga]

  implicit object InvoicingSagaActorFactory extends SagaActorFactory[InvoicingSaga] {
    def props(pc: PassivationConfig): Props = {
      Props(new InvoicingSaga(pc, invoiceOffice.actorPath, Some(schedulingOffice.actorPath)))
    }
  }

  //
  // SagaManager factory
  //

  implicit def sagaManagerFactory[E <: Saga]: SagaManagerFactory[E] = sagaOffice => {
    new SagaManager()(sagaOffice) with EventstoreSubscriber
  }

  //
  // Receptor factory
  //

  implicit val receptorFactory: ReceptorFactory = receptorConfig => {
    new Receptor with EventstoreSubscriber {
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
