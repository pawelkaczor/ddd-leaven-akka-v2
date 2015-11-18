package ecommerce.invoicing

import ecommerce.invoicing.Invoice._
import ecommerce.sales._
import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.aggregate.{AggregateRoot, AggregateState}
import pl.newicom.dddd.eventhandling.EventPublisher
import pl.newicom.dddd.office.LocalOfficeId.fromRemoteId

object Invoice {

 case class State(amountPaid: Option[Money]) extends AggregateState[State] {
    override def apply = {
      case OrderBilled(_, _, amount, _) =>
        copy(amountPaid = Some(amountPaid.getOrElse(Money()) + amount))
      case OrderBillingFailed(_, _) =>
        this
    }
  }

  implicit val officeId = fromRemoteId[Invoice](InvoicingOfficeId)
}

abstract class Invoice(override val pc: PassivationConfig) extends AggregateRoot[State, Invoice] {
  this: EventPublisher =>

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
        raise(OrderBilled(invoiceId, orderId, amount, paymentId))
      } else {
        throw new RuntimeException(s"Unknown invoice")
      }

    case CancelInvoice(invoiceId, orderId) =>
      if (initialized) {
        raise(OrderBillingFailed(invoiceId, orderId))
      } else {
        throw new RuntimeException(s"Unknown invoice")
      }

  }

}
