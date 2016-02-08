package ecommerce.sales.app

import java.net.InetAddress

import akka.actor._
import com.typesafe.config.Config
import ecommerce.sales.{ReservationConfirmed, OrderSaga, Reservation}
import org.slf4j.Logger
import pl.newicom.dddd.actor.{CreationSupport, PassivationConfig}
import pl.newicom.dddd.aggregate._
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.eventhandling.EventPublisher
import pl.newicom.dddd.messaging.{AddressableMessage, Message}
import pl.newicom.dddd.messaging.event.{EventMessage, OfficeEventMessage}
import pl.newicom.dddd.monitoring.{AggregateRootMonitoring, SagaMonitoring, ReceptorMonitoring}
import pl.newicom.dddd.office.Office
import pl.newicom.dddd.persistence.PersistentActorLogging
import pl.newicom.dddd.process.SagaSupport._
import pl.newicom.dddd.process._
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

    override def props(pc: PassivationConfig) = Props(new Reservation(pc) with LocalPublisher with AggregateRootMonitoring {

      override def toEventMessage(event: DomainEvent): EventMessage = {
        event match {
          case rc: ReservationConfirmed =>
            EventMessage(event)
              .withMetaAttribute("commandTimestamp", commandTraceContext.startTimestamp.nanos)
              .withMetaAttribute("commandName", "ConfirmReservation")
          case _ =>
            super.toEventMessage(event)
        }
      }
    })
  }

  implicit object ReservationShardResolution extends DefaultShardResolution[Reservation]

  implicit object OrderSagaShardResolution extends DefaultShardResolution[OrderSaga]

  implicit object OrderSagaActorFactory extends SagaActorFactory[OrderSaga] {

    def props(pc: PassivationConfig): Props = {
      Props(new OrderSaga(pc, reservationOffice.actorPath) with SagaMonitoring {

        override protected def acknowledgeEvent(em: Message): Unit = {
          for {
            commandTimestamp <- em.tryGetMetaAttribute[BigInt]("commandTimestamp")
            commandName <- em.tryGetMetaAttribute[String]("commandName")
          } yield {
            val eventName = em.asInstanceOf[AddressableMessage].payloadName
            val contextName = s"AR-$commandName-Saga-$eventName"
            newTraceContext(contextName, commandTimestamp.toLong).foreach(_.finish())
          }
          super.acknowledgeEvent(em)
        }

        override def onEventReceived(em: EventMessage, appliedAction: SagaAction): Unit = {
          super.onEventReceived(em, appliedAction)
        }
      })
    }
  }

  //
  // SagaManager factory
  //

  implicit def sagaManagerFactory[E <: Saga]: SagaManagerFactory[E] = (sagaOffice) => {
    new SagaManager[E]()(sagaOffice) with EventstoreSubscriber with ReceptorMonitoring
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
