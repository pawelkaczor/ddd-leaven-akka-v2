package ecommerce.shipping

import java.util.UUID

import akka.actor.ActorPath
import ecommerce.invoicing.{InvoicingOffice, OrderBilled}
import pl.newicom.dddd.messaging.command.CommandMessage
import pl.newicom.dddd.messaging.event.EventMessage
import pl.newicom.dddd.process.{ReceptorBuilder, ReceptorConfig}

object PaymentReceptor {

  def apply(shippingOffice: ActorPath): ReceptorConfig = ReceptorBuilder()
    .reactTo[InvoicingOffice]
    .applyTransduction {
      case EventMessage(_, OrderBilled(invoiceId, orderId, amount, paymentId)) =>
        CommandMessage(CreateShipment(UUID.randomUUID().toString, orderId))
    }
    .propagateTo(shippingOffice)
}
