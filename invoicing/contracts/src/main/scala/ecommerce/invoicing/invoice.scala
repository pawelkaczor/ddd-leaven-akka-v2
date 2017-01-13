package ecommerce.invoicing

import ecommerce.sales.Money
import org.joda.time.DateTime
import pl.newicom.dddd.aggregate
import pl.newicom.dddd.aggregate._

//
// Commands
//
sealed trait Command extends aggregate.Command {
  def invoiceId: EntityId
  override def aggregateId = invoiceId
}

case class CreateInvoice(invoiceId: EntityId, orderId: EntityId, customerId: EntityId, totalAmount: Money, createEpoch: DateTime) extends Command
case class ReceivePayment(invoiceId: EntityId, orderId: EntityId, amount: Money, paymentId: EntityId) extends Command
case class CancelInvoice(invoiceId: EntityId, orderId: EntityId) extends Command

//
// Events
//
sealed trait Event

case class InvoiceCreated(invoiceId: EntityId, orderId: EntityId, customerId: EntityId, totalAmount: Money, createEpoch: DateTime) extends Event
case class OrderBilled(invoiceId: EntityId, orderId: EntityId, amount: Money, paymentId: EntityId) extends Event
case class OrderBillingFailed(invoiceId: EntityId, orderId: EntityId) extends Event
case class PaymentExpired(invoiceId: EntityId, orderId: EntityId) extends Event
