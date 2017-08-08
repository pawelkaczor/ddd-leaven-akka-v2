package ecommerce.shipping.app

import akka.actor._
import akka.kernel.Bootable
import ecommerce.shipping.{Event, Shipment}
import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.aggregate.{AggregateRootActorFactory, AggregateRootLogger, DefaultConfig}

trait ShippingBackendConfiguration {
  this: Bootable =>

  implicit object ShipmentARFactory extends AggregateRootActorFactory[Shipment] {
    override def props(pc: PassivationConfig) =
      Props(new Shipment(DefaultConfig(pc, replyWithEvents = false)) with AggregateRootLogger[Event])
  }

}
