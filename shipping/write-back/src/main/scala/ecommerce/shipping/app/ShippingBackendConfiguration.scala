package ecommerce.shipping.app

import akka.actor._
import akka.kernel.Bootable
import ecommerce.shipping.Department
import ecommerce.shipping.{Event, Shipment}
import pl.newicom.dddd.actor.{ActorFactory, PassivationConfig}
import pl.newicom.dddd.aggregate.{AggregateRootActorFactory, AggregateRootLogger, DefaultConfig}
import pl.newicom.dddd.coordination.ReceptorConfig
import pl.newicom.dddd.office.LocalOfficeId
import pl.newicom.dddd.process.CommandReceptorSupport.CommandReception
import pl.newicom.dddd.process.{Receptor, ReceptorActorFactory}
import pl.newicom.eventstore.EventstoreSubscriber

trait ShippingBackendConfiguration {
  this: Bootable =>

  implicit object ShipmentARFactory extends AggregateRootActorFactory[Shipment] {
    override def props(pc: PassivationConfig) =
      Props(new Shipment(DefaultConfig(pc, replyWithEvents = false)) with AggregateRootLogger[Event])
  }

  implicit def commandReceptorActorFactory[A <: CommandReception : LocalOfficeId : ActorFactory]: ReceptorActorFactory[A] = new ReceptorActorFactory[A] {
    def receptorFactory: ReceptorFactory = (config: ReceptorConfig) => new Receptor(config) with EventstoreSubscriber
  }

  val commandReception: CommandReception = CommandReception(Department)

}
