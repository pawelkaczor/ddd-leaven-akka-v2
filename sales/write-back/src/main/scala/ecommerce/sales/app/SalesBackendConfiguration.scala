package ecommerce.sales.app

import akka.actor._
import akka.kernel.Bootable
import ecommerce.sales.{Event, ReservationAggregateRoot}
import pl.newicom.dddd.actor.{ActorFactory, DefaultConfig, PassivationConfig}
import pl.newicom.dddd.aggregate._
import pl.newicom.dddd.coordination.ReceptorConfig
import pl.newicom.dddd.monitoring.AggregateRootMonitoring
import pl.newicom.dddd.office.LocalOfficeId
import pl.newicom.dddd.process.CommandReceptorSupport.CommandReception
import pl.newicom.dddd.process.{Receptor, ReceptorActorFactory}
import pl.newicom.eventstore.EventstoreSubscriber

trait SalesBackendConfiguration {
  this: Bootable =>

  implicit object ReservationARFactory extends AggregateRootActorFactory[ReservationAggregateRoot] {
    override def props(pc: PassivationConfig) =
      Props(new ReservationAggregateRoot(DefaultConfig(pc, replyWithEvents = false)) with AggregateRootMonitoring with AggregateRootLogger[Event])
  }

  implicit def commandReceptorActorFactory[A <: CommandReception : LocalOfficeId : ActorFactory]: ReceptorActorFactory[A] = new ReceptorActorFactory[A] {
    def receptorFactory: ReceptorFactory = (config: ReceptorConfig) => new Receptor(config) with EventstoreSubscriber
  }

}
