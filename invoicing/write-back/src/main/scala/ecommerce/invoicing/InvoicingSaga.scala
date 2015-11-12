package ecommerce.invoicing

import akka.actor.ActorPath
import ecommerce.invoicing.InvoicingSaga.InvoicingSagaConfig
import ecommerce.sales.{Money, ReservationConfirmed}
import org.joda.time.DateTime.now
import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.process.{DropEvent, ProcessEvent, Saga, SagaConfig}

object InvoicingSaga {
  object InvoiceStatus extends Enumeration {
    type InvoiceStatus = Value
    val New, WaitingForPayment, Completed, Failed = Value
  }

  implicit object InvoicingSagaConfig extends SagaConfig[InvoicingSaga]("invoicing") {
    def correlationIdResolver = {
      case ReservationConfirmed(reservationId, _, _) => reservationId
      case OrderBilled(invoiceId, _, _, _) => invoiceId
      case OrderBillingFailed(invoiceId, _) => invoiceId
    }
  }

}

import ecommerce.invoicing.InvoicingSaga.InvoiceStatus._

class InvoicingSaga(val pc: PassivationConfig, invoicingOffice: ActorPath, override val schedulingOffice: Option[ActorPath]) extends Saga {

  override def persistenceId = s"${InvoicingSagaConfig.name}Saga-$id"

  var status = New

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

  def applyEvent = {
    case ReservationConfirmed(reservationId, customerId, totalAmountOpt) =>
      val totalAmount = totalAmountOpt.getOrElse(Money())
      deliverCommand(invoicingOffice, CreateInvoice(sagaId, reservationId, customerId, totalAmount, now()))
      status = WaitingForPayment
      // schedule payment deadline
      schedule(PaymentExpired(sagaId, reservationId), now.plusMinutes(3))

    case PaymentExpired(invoiceId, orderId) =>
      // cancel invoice
      log.debug("Payment expired for order '{}'.", orderId)
      deliverCommand(invoicingOffice, CancelInvoice(invoiceId, orderId))

    case OrderBilled(_, orderId, _, _) =>
      log.debug("InvoicingSaga for order '{}' completed.", orderId)
      status = Completed

    case OrderBillingFailed(_, orderId) =>
      log.debug("InvoicingSaga for order '{}' failed.", orderId)
      status = Failed
  }
}
