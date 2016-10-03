package ecommerce.invoicing.app

import java.net.InetAddress

import akka.actor._
import akka.kernel.Bootable
import com.typesafe.config.Config
import ecommerce.invoicing.{Invoice, InvoicingOfficeId, InvoicingSaga}
import org.slf4j.Logger
import pl.newicom.dddd.actor.{CreationSupport, PassivationConfig}
import pl.newicom.dddd.aggregate.{AggregateRootActorFactory, BusinessEntity}
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.eventhandling.NoPublishing
import pl.newicom.dddd.monitoring.{AggregateRootMonitoring, ReceptorMonitoring, SagaMonitoring}
import pl.newicom.dddd.office.OfficeFactory.office
import pl.newicom.dddd.office.{Office, OfficeFactory}
import pl.newicom.dddd.process.ReceptorSupport.ReceptorFactory
import pl.newicom.dddd.process.SagaSupport.SagaManagerFactory
import pl.newicom.dddd.process.{Saga, SagaActorFactory, SagaManager}
import pl.newicom.dddd.scheduling.{CurrentDeadlinesOfficeId, DeadlinesReceptor, Scheduler}
import pl.newicom.eventstore.EventstoreSubscriber

import scala.concurrent.duration.{Duration, DurationInt}
import scala.io.Source
import scala.util.Try

trait InvoicingBackendConfiguration {
  this: Bootable =>

  def creationSupport = implicitly[CreationSupport]

  implicit def shardResolution[A <: BusinessEntity] = new DefaultShardResolution[A]

  //
  // Scheduling
  //
  implicit object SchedulerFactory extends AggregateRootActorFactory[Scheduler] {
    override def props(pc: PassivationConfig) = Props(new Scheduler(pc) with NoPublishing with AggregateRootMonitoring {
      override def id = "global"
    })
  }

  //
  // Invoicing
  //
  implicit object InvoiceARFactory extends AggregateRootActorFactory[Invoice] {
    override def props(pc: PassivationConfig) = Props(new Invoice(pc) with NoPublishing)
  }

  implicit object InvoicingSagaActorFactory extends SagaActorFactory[InvoicingSaga] {
    // avoid Saga passivation during a stress-test as it results in a delivery failure of DeliveryTick (ALOD) messages
    override def inactivityTimeout: Duration = 30 minutes

    def props(pc: PassivationConfig): Props = {
      Props(new InvoicingSaga(pc, office(InvoicingOfficeId), Some(office(CurrentDeadlinesOfficeId))) with SagaMonitoring)
    }
  }

  implicit def sagaManagerFactory[E <: Saga]: SagaManagerFactory[E] = sagaOffice => {
    new SagaManager()(sagaOffice) with EventstoreSubscriber with ReceptorMonitoring
  }

  implicit val receptorFactory: ReceptorFactory = receptorConfig => {
    new DeadlinesReceptor with EventstoreSubscriber with ReceptorMonitoring {
      def config = receptorConfig
    }
  }

}
