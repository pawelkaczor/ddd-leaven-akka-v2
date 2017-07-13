package ecommerce.invoicing

import ecommerce.sales._
import pl.newicom.dddd.aggregate._
import pl.newicom.dddd.office.LocalOfficeId
import pl.newicom.dddd.office.LocalOfficeId.fromRemoteId

object Invoice extends AggregateRootSupport {

  sealed trait Invoicing extends AggregateActions[Event, Invoicing, Config]

  implicit case object Uninitialized extends Invoicing with Uninitialized[Invoicing] {

    def actions: Actions =
      handleCommand {
        case CreateInvoice(invoiceId, orderId, customerId, totalAmount, createEpoch) =>
          InvoiceCreated(invoiceId, orderId, customerId, totalAmount, createEpoch)
      }
      .handleEvent {
        case InvoiceCreated(_, _, _, _, _) =>
          Active(amountPaid = None)
      }
  }

 case class Active(amountPaid: Option[Money]) extends Invoicing {

   def actions: Actions =
     handleCommand {
       case ReceivePayment(invoiceId, orderId, amount, paymentId) =>
         OrderBilled(invoiceId, orderId, amount, paymentId)

       case CancelInvoice(invoiceId, orderId) =>
         OrderBillingFailed(invoiceId, orderId)
     }
     .handleEvent {
        case OrderBilled(_, _, amount, _) =>
          copy(amountPaid = Some(amountPaid.getOrElse(Money()) + amount))
        case OrderBillingFailed(_, _) =>
          this
      }
  }

  implicit val officeId: LocalOfficeId[Invoice] = fromRemoteId[Invoice](InvoicingOfficeId)
}

import ecommerce.invoicing.Invoice._

class Invoice(val config: Config) extends AggregateRoot[Event, Invoicing, Invoice] with ConfigClass[Config]