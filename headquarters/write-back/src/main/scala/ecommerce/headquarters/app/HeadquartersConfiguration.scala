package ecommerce.headquarters.app

import java.util.UUID

import akka.actor.{Props, _}
import com.typesafe.config.Config
import ecommerce.headquarters.app.HeadquartersConfiguration._
import ecommerce.headquarters.processes.OrderProcessManager
import ecommerce.shipping.ShipmentId
import org.slf4j.Logger
import pl.newicom.dddd.actor.{ActorFactory, PassivationConfig}
import pl.newicom.dddd.aggregate.{AggregateRootActorFactory, AggregateRootLogger, DefaultConfig}
import pl.newicom.dddd.coordination.ReceptorConfig
import pl.newicom.dddd.office.LocalOfficeId
import pl.newicom.dddd.process._
import pl.newicom.dddd.scheduling.{Scheduler, SchedulerEvent, schedulingOfficeId}
import pl.newicom.eventstore.EventstoreSubscriber

import scala.concurrent.duration._

object HeadquartersConfiguration {
  val HQDepartment: String = "Headquarters"
}

trait HeadquartersConfiguration {

  def log: Logger
  def config: Config
  implicit def system: ActorSystem

  implicit val schedulingOfficeID: LocalOfficeId[Scheduler] = schedulingOfficeId(HQDepartment)
  implicit val commandQueueOfficeID: LocalOfficeId[CommandSink] = commandSinkOfficeId(HQDepartment)

  implicit object SchedulerFactory extends AggregateRootActorFactory[Scheduler] {
    override def props(pc: PassivationConfig) = Props(new Scheduler(DefaultConfig(pc, replyWithEvents = false)) with AggregateRootLogger[SchedulerEvent] {
      // TODO not needed
      override def id = "global"
    })
  }

  implicit object CommandSinkFactory extends AggregateRootActorFactory[CommandSink] {
    override def props(pc: PassivationConfig) = Props(new CommandSink(DefaultConfig(pc, replyWithEvents = false)) with AggregateRootLogger[CommandEnqueued])
  }

  implicit object OrderProcessManagerActorFactory extends SagaActorFactory[OrderProcessManager] {
    def props(pc: PassivationConfig): Props =
      Props(new OrderProcessManager(pc, () => new ShipmentId(UUID.randomUUID().toString)))
  }

  implicit def receptorActorFactory[A : LocalOfficeId : ActorFactory]: ReceptorActorFactory[A] = new ReceptorActorFactory[A] {
    def receptorFactory: ReceptorFactory = (config: ReceptorConfig) => {
      new Receptor(config.copy(capacity = 100)) with EventstoreSubscriber {
        override def redeliverInterval: FiniteDuration = 10.seconds
      }
    }
  }

}
