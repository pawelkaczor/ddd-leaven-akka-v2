package ecommerce.invoicing

import akka.actor.ActorPath
import ecommerce.invoicing.InvoicingSaga._
import ecommerce.sales.{Money, ReservationConfirmed}
import org.joda.time.DateTime.now
import pl.newicom.dddd.actor.PassivationConfig
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

class InvoicingSaga(val pc: PassivationConfig, invoicingOffice: ActorPath, override val schedulingOffice: Option[ActorPath])
  extends Saga with StateHandling[InvoiceStatus] {

  override def persistenceId = s"${InvoicingSagaConfig.name}Saga-$id"

  val initialState = New

  def status = state

  def receiveEvent = {
    case e: ReservationConfirmed if status == New =>
      ProcessEvent
    case e: OrderBilled if status == WaitingForPayment =>
      ProcessEvent
    case e: PaymentExpired =>
      if (status == WaitingForPayment) {
        ProcessEvent
      } else {
        DropEvent
      }
    case e: OrderBillingFailed if status == WaitingForPayment =>
      ProcessEvent
  }

  def stateMachine: StateMachine = {

    case New => {

      case ReservationConfirmed(reservationId, customerId, totalAmountOpt) =>
        val totalAmount = totalAmountOpt.getOrElse(Money())
        deliverCommand(invoicingOffice, CreateInvoice(sagaId, reservationId, customerId, totalAmount, now()))
        // schedule payment deadline
        schedule(PaymentExpired(sagaId, reservationId), now.plusMinutes(3))

        WaitingForPayment

    }

    case WaitingForPayment => {

      case PaymentExpired(invoiceId, orderId) =>
        // cancel invoice
        log.debug("Payment expired for order '{}'.", orderId)
        deliverCommand(invoicingOffice, CancelInvoice(invoiceId, orderId))
        state

      case OrderBilled(_, orderId, _, _) =>
        log.debug("InvoicingSaga for order '{}' completed.", orderId)
        Completed

      case OrderBillingFailed(_, orderId) =>
        log.debug("InvoicingSaga for order '{}' failed.", orderId)
        Failed
    }

  }
}
