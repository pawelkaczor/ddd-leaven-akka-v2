package ecommerce.shipping.app

import akka.actor._
import akka.kernel.Bootable
import ecommerce.shipping.Shipment
import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.aggregate.{AggregateRootActorFactory, DefaultConfig}

trait ShippingBackendConfiguration {
  this: Bootable =>

  implicit object ShipmentARFactory extends AggregateRootActorFactory[Shipment] {
    override def props(pc: PassivationConfig) = Props(new Shipment(DefaultConfig(pc, replyWithEvents = false)))
  }

}
