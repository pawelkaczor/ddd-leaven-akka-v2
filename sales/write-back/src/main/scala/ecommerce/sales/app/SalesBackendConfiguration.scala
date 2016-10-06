package ecommerce.sales.app

import akka.actor._
import akka.kernel.Bootable
import ecommerce.sales.Reservation
import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.aggregate._
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.eventhandling.NoPublishing
import pl.newicom.dddd.monitoring.AggregateRootMonitoring

trait SalesBackendConfiguration {
  this: Bootable =>

  implicit def shardResolution[A <: BusinessEntity] = new DefaultShardResolution[A]

  implicit object ReservationARFactory extends AggregateRootActorFactory[Reservation] {
    override def props(pc: PassivationConfig) = Props(new Reservation(pc) with NoPublishing with AggregateRootMonitoring)
  }

}
