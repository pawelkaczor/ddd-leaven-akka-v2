package ecommerce.sales.app

import akka.actor._
import akka.kernel.Bootable
import ecommerce.sales.{OrderSaga, Reservation, ReservationOfficeId}
import pl.newicom.dddd.actor.{CreationSupport, PassivationConfig}
import pl.newicom.dddd.aggregate._
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.eventhandling.NoPublishing
import pl.newicom.dddd.monitoring.{AggregateRootMonitoring, ReceptorMonitoring, SagaMonitoring}
import pl.newicom.dddd.office.OfficeFactory.office
import pl.newicom.dddd.process.SagaSupport.SagaManagerFactory
import pl.newicom.dddd.process._
import pl.newicom.eventstore.EventstoreSubscriber

import scala.concurrent.duration.{Duration, DurationInt}

trait SalesBackendConfiguration {
  this: Bootable =>

  def creationSupport = implicitly[CreationSupport]

  implicit def shardResolution[A <: BusinessEntity] = new DefaultShardResolution[A]

  implicit object ReservationARFactory extends AggregateRootActorFactory[Reservation] {
    override def props(pc: PassivationConfig) = Props(new Reservation(pc) with NoPublishing with AggregateRootMonitoring)
  }

  implicit object OrderSagaActorFactory extends SagaActorFactory[OrderSaga] {

    // avoid Saga passivation during a stress-test as it results in a delivery failure of DeliveryTick (ALOD) messages
    override def inactivityTimeout: Duration = 30 minutes

    def props(pc: PassivationConfig): Props = {
      Props(new OrderSaga(pc, office(ReservationOfficeId)) with SagaMonitoring {
/*
        val rejectionPercent: Int = 0 // increase for testing purposes only

        override def receiveEvent: ReceiveEvent = {
          case e: DomainEvent if (Math.random() * 100) < rejectionPercent => RejectEvent
          case e: DomainEvent => RaiseEvent(e)
        }
*/
      })
    }
  }

  implicit def sagaManagerFactory[E <: Saga]: SagaManagerFactory[E] = (sagaOffice) => {
    new SagaManager[E]()(sagaOffice) with EventstoreSubscriber with ReceptorMonitoring {
      override lazy val config = defaultConfig.copy(capacity = 100)
      override def redeliverInterval = 10.seconds
    }
  }

}
