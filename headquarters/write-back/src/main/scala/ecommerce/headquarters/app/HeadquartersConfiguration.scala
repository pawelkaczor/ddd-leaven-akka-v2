package ecommerce.headquarters.app

import akka.actor.{Props, _}
import com.typesafe.config.Config
import ecommerce.invoicing.InvoicingOfficeId
import ecommerce.headquarters.processes.OrderProcessManager
import ecommerce.sales.ReservationOfficeId
import ecommerce.shipping.ShippingOfficeId
import org.slf4j.Logger
import pl.newicom.dddd
import pl.newicom.dddd.actor.{CreationSupport, PassivationConfig}
import pl.newicom.dddd.aggregate.{AggregateRootActorFactory, BusinessEntity}
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.eventhandling.NoPublishing
import pl.newicom.dddd.office.LocalOfficeId
import pl.newicom.dddd.office.OfficeFactory._
import pl.newicom.dddd.process.ReceptorSupport._
import pl.newicom.dddd.process.SagaSupport.SagaManagerFactory
import pl.newicom.dddd.process.{Saga, SagaActorFactory, SagaManager}
import pl.newicom.dddd.scheduling.{DeadlinesReceptor, Scheduler}
import pl.newicom.eventstore.EventstoreSubscriber

import scala.concurrent.duration.DurationInt

trait HeadquartersConfiguration {

  def log: Logger
  def config: Config
  implicit def system: ActorSystem
  def creationSupport = implicitly[CreationSupport]

  implicit def shardResolution[A <: BusinessEntity] = new DefaultShardResolution[A]

  implicit val schedulingOfficeID: LocalOfficeId[Scheduler] = dddd.scheduling.schedulingOfficeId("Headquarters")

  implicit object SchedulerFactory extends AggregateRootActorFactory[Scheduler] {
    override def props(pc: PassivationConfig) = Props(new Scheduler(pc) with NoPublishing {
      override def id = "global"
    })
  }

  implicit object OrderProcessManagerActorFactory extends SagaActorFactory[OrderProcessManager] {

    def props(pc: PassivationConfig): Props = {
      val reservationOffice = office(ReservationOfficeId)
      val invoicingOffice   = office(InvoicingOfficeId)
      val shippingOffice    = office(ShippingOfficeId)
      val schedulingOffice  = office[Scheduler]

      Props(new OrderProcessManager(reservationOffice, invoicingOffice, shippingOffice, pc, Some(schedulingOffice)))
    }
  }

  implicit def sagaManagerFactory[E <: Saga]: SagaManagerFactory[E] = (sagaOffice) => {
    new SagaManager[E]()(sagaOffice) with EventstoreSubscriber {
      override lazy val config = defaultConfig.copy(capacity = 100)
      override def redeliverInterval = 10.seconds
    }
  }

  implicit val receptorFactory: ReceptorFactory = receptorConfig => {
    new DeadlinesReceptor with EventstoreSubscriber {
      def config = receptorConfig
    }
  }

}
