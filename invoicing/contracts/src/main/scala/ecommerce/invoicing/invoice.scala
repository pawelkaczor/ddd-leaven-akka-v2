package ecommerce.invoicing

import ecommerce.sales.Money
import org.joda.time.DateTime
import pl.newicom.dddd.aggregate
import pl.newicom.dddd.aggregate._

//
// Commands
//
sealed trait Command extends aggregate.Command {
  def invoiceId: InvoiceId
  override def aggregateId: InvoiceId = invoiceId
}

case class CreateInvoice(invoiceId: InvoiceId, orderId: EntityId, customerId: EntityId, totalAmount: Money, createEpoch: DateTime) extends Command
case class ReceivePayment(invoiceId: InvoiceId, orderId: EntityId, amount: Money, paymentId: EntityId) extends Command
case class CancelInvoice(invoiceId: InvoiceId, orderId: EntityId) extends Command

//
// Events
//
sealed trait Event

case class InvoiceCreated(invoiceId: InvoiceId, orderId: EntityId, customerId: EntityId, totalAmount: Money, createEpoch: DateTime) extends Event
case class OrderBilled(invoiceId: InvoiceId, orderId: EntityId, amount: Money, paymentId: EntityId) extends Event
case class OrderBillingFailed(invoiceId: InvoiceId, orderId: EntityId) extends Event
case class PaymentExpired(invoiceId: InvoiceId, orderId: EntityId) extends Event
