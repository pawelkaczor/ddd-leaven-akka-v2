package ecommerce.sales.app

import java.net.InetAddress

import akka.actor._
import com.typesafe.config.Config
import ecommerce.sales.{OrderSaga, Reservation}
import org.slf4j.Logger
import pl.newicom.dddd.actor.{CreationSupport, PassivationConfig}
import pl.newicom.dddd.aggregate._
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.eventhandling.NoPublishing
import pl.newicom.dddd.monitoring.{AggregateRootMonitoring, ReceptorMonitoring, SagaMonitoring}
import pl.newicom.dddd.office.Office
import pl.newicom.dddd.process.SagaSupport._
import pl.newicom.dddd.process._
import pl.newicom.eventstore.EventstoreSubscriber

import scala.concurrent.duration.{DurationInt, Duration}
import scala.io.Source
import scala.util.Try

trait SalesBackendConfiguration {

  def log: Logger
  def config: Config
  implicit def system: ActorSystem
  def creationSupport = implicitly[CreationSupport]
  def reservationOffice: Office[Reservation]

  //
  // Reservation Office
  //
  implicit object ReservationARFactory extends AggregateRootActorFactory[Reservation] {
    override def props(pc: PassivationConfig) = Props(new Reservation(pc) with NoPublishing with AggregateRootMonitoring)
  }

  implicit object ReservationShardResolution extends DefaultShardResolution[Reservation]

  implicit object OrderSagaShardResolution extends DefaultShardResolution[OrderSaga]

  implicit object OrderSagaActorFactory extends SagaActorFactory[OrderSaga] {

    // avoid Saga passivation during a stress-test as it results in a delivery failure of DeliveryTick (ALOD) messages
    override def inactivityTimeout: Duration = 30 minutes

    def props(pc: PassivationConfig): Props = {
      Props(new OrderSaga(pc, reservationOffice.actorPath) with SagaMonitoring {
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

  def seeds(config: Config) = {
    // Read cluster seed nodes from the file specified in the configuration
    Try(config.getString("app.cluster.seedsFile")).toOption match {
      case Some(seedsFile) =>
        // Seed file was specified, read it
        log.info(s"reading seed nodes from file: $seedsFile")
        Source.fromFile(seedsFile).getLines().map { address =>
          AddressFromURIString.parse(s"akka.tcp://sales@$address")
        }.toList
      case None =>
        // No seed file specified, use this node as the first seed
        log.info("no seed file found, using default seeds")
        val port = config.getInt("app.port")
        val localAddress = Try(config.getString("app.host"))
          .toOption.getOrElse(InetAddress.getLocalHost.getHostAddress)
        List(AddressFromURIString.parse(s"akka.tcp://sales@$localAddress:$port"))
    }
  }

}
