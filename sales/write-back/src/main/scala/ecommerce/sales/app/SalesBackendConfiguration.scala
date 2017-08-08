package ecommerce.sales.app

import akka.actor._
import akka.kernel.Bootable
import ecommerce.sales.{Event, ReservationAggregateRoot}
import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.aggregate._
import pl.newicom.dddd.monitoring.AggregateRootMonitoring

trait SalesBackendConfiguration {
  this: Bootable =>

  implicit object ReservationARFactory extends AggregateRootActorFactory[ReservationAggregateRoot] {
    override def props(pc: PassivationConfig) =
      Props(new ReservationAggregateRoot(DefaultConfig(pc, replyWithEvents = false)) with AggregateRootMonitoring with AggregateRootLogger[Event])
  }

}
