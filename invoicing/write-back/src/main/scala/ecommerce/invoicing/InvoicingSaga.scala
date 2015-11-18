package ecommerce.invoicing

import akka.actor.ActorPath
import ecommerce.invoicing.InvoicingSaga._
import ecommerce.sales.{Money, ReservationConfirmed}
import org.joda.time.DateTime.now
import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.office.SagaConfig
import pl.newicom.dddd.process._

object InvoicingSaga {

  sealed trait InvoiceStatus extends SagaState[InvoiceStatus]
  case object New extends InvoiceStatus
  case object WaitingForPayment extends InvoiceStatus
  case object Completed extends InvoiceStatus
  case object Failed extends InvoiceStatus


  implicit object InvoicingSagaConfig extends SagaConfig[InvoicingSaga]("invoicing") {
    def correlationIdResolver = {
      case ReservationConfirmed(reservationId, _, _) => reservationId
      case OrderBilled(invoiceId, _, _, _) => invoiceId
      case OrderBillingFailed(invoiceId, _) => invoiceId
    }
  }

}

class InvoicingSaga(val pc: PassivationConfig,
                    invoicingOffice: ActorPath,
                    override val schedulingOffice: Option[ActorPath]) extends ProcessManager[InvoiceStatus] {

  def officeId = InvoicingSagaConfig

  override def receiveEvent =
    super.receiveEvent.orElse {
      case e: PaymentExpired if state != WaitingForPayment => DropEvent
    }

  startWhen {

    case _:ReservationConfirmed => New

  } andThen {

    case New => {

      case ReservationConfirmed(reservationId, customerId, totalAmountOpt) =>
        val totalAmount = totalAmountOpt.getOrElse(Money())
        deliverCommand(invoicingOffice, CreateInvoice(sagaId, reservationId, customerId, totalAmount, now()))
        // schedule payment deadline
        schedule(PaymentExpired(sagaId, reservationId), now.plusMinutes(3))

        goto(WaitingForPayment)

    }

    case WaitingForPayment => {

      case PaymentExpired(invoiceId, orderId) =>
        // cancel invoice
        log.debug("Payment expired for order '{}'.", orderId)
        deliverCommand(invoicingOffice, CancelInvoice(invoiceId, orderId))
        stay()

      case OrderBilled(_, orderId, _, _) =>

        goto(Completed)

      case OrderBillingFailed(_, orderId) =>

        goto(Failed)
    }

  }

}
