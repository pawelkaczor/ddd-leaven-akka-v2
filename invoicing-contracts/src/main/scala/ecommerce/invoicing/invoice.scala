package ecommerce.invoicing

import ecommerce.sales.Money
import org.joda.time.DateTime
import pl.newicom.dddd.aggregate
import pl.newicom.dddd.aggregate._

trait InvoicingOffice

//
// Commands
//
sealed trait Command extends aggregate.Command {
  def invoiceId: EntityId
  override def aggregateId = invoiceId
}

case class CreateInvoice(invoiceId: EntityId, orderId: EntityId, customerId: EntityId, totalAmount: Money, createEpoch: DateTime) extends Command
case class ReceivePayment(invoiceId: EntityId, orderId: EntityId, amount: Money, paymentId: EntityId) extends Command

//
// Events
//
case class InvoiceCreated(invoiceId: EntityId, orderId: EntityId, customerId: EntityId, totalAmount: Money, createEpoch: DateTime)
case class PaymentReceived(invoiceId: EntityId, orderId: EntityId, amount: Money, paymentId: EntityId)
