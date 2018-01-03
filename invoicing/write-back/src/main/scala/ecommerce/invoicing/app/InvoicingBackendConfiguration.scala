package ecommerce.invoicing.app

import akka.actor._
import akka.kernel.Bootable
import ecommerce.invoicing.{Event, Invoice}
import pl.newicom.dddd.actor.{ActorFactory, DefaultConfig, PassivationConfig}
import pl.newicom.dddd.aggregate.{AggregateRootActorFactory, AggregateRootLogger}
import pl.newicom.dddd.coordination.ReceptorConfig
import pl.newicom.dddd.office.LocalOfficeId
import pl.newicom.dddd.process.CommandReceptorSupport.CommandReception
import pl.newicom.dddd.process.{Receptor, ReceptorActorFactory}
import pl.newicom.eventstore.EventstoreSubscriber

trait InvoicingBackendConfiguration {
  this: Bootable =>

  implicit object InvoiceARFactory extends AggregateRootActorFactory[Invoice] {
    override def props(pc: PassivationConfig) =
      Props(new Invoice(DefaultConfig(pc, replyWithEvents = false)) with AggregateRootLogger[Event])
  }

  implicit def commandReceptorActorFactory[A <: CommandReception : LocalOfficeId : ActorFactory]: ReceptorActorFactory[A] = new ReceptorActorFactory[A] {
    def receptorFactory: ReceptorFactory = (config: ReceptorConfig) => new Receptor(config) with EventstoreSubscriber
  }

}
