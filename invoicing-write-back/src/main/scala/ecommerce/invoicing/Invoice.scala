package ecommerce.invoicing

import ecommerce.invoicing.Invoice._
import ecommerce.sales._
import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.aggregate.{AggregateRoot, AggregateState}
import pl.newicom.dddd.eventhandling.EventPublisher

object Invoice {

 case class State(amountPaid: Option[Money]) extends AggregateState {
    override def apply = {
      case PaymentReceived(_, _, amount, _) =>
        copy(amountPaid = Some(amountPaid.getOrElse(Money()) + amount))
    }
  }
}

abstract class Invoice(override val pc: PassivationConfig) extends AggregateRoot[State] {
  this: EventPublisher =>

  override def persistenceId = s"${invoicingOffice.name}-$id"

  override val factory: AggregateRootFactory = {
    case InvoiceCreated(_, _, _, _, _) =>
      State(None)
  }

  override def handleCommand: Receive = {
    case CreateInvoice(invoiceId, orderId, customerId, totalAmount, createEpoch) =>
      if (initialized) {
        throw new RuntimeException(s"Invoice $invoiceId already exists")
      } else {
        raise(InvoiceCreated(invoiceId, orderId, customerId, totalAmount, createEpoch))
      }

    case ReceivePayment(invoiceId, orderId, amount, paymentId) =>
      if (initialized) {
        raise(PaymentReceived(invoiceId, orderId, amount, paymentId))
      } else {
        throw new RuntimeException(s"Unknown invoice")
      }
  }

}
