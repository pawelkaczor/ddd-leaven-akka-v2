package ecommerce.shipping.app

import akka.actor._
import akka.kernel.Bootable
import ecommerce.shipping.Shipment
import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.aggregate.AggregateRootActorFactory
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.eventhandling.EventPublisher
import pl.newicom.dddd.messaging.event.OfficeEventMessage
import pl.newicom.dddd.persistence.PersistentActorLogging


trait LocalPublisher extends EventPublisher {
  this: Actor with PersistentActorLogging =>

  override def publish(em: OfficeEventMessage): Unit = {
    context.system.eventStream.publish(em.event)
    log.debug(s"Published: $em")
  }
}

trait ShippingBackendConfiguration {
  this: Bootable =>

  implicit object ShipmentARFactory extends AggregateRootActorFactory[Shipment] {
    override def props(pc: PassivationConfig) = Props(new Shipment(pc) with LocalPublisher)
  }

  implicit object ShippingShardResolution extends DefaultShardResolution[Shipment]

}
