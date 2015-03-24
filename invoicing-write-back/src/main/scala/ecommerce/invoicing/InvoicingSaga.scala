package ecommerce.invoicing

import java.util.UUID

import akka.actor.ActorPath
import ecommerce.invoicing.InvoicingSaga.InvoicingSagaConfig
import ecommerce.sales.{Money, ReservationConfirmed, salesOffice}
import org.joda.time.DateTime.now
import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.messaging.event.EventMessage
import pl.newicom.dddd.process.{Saga, SagaConfig}
import pl.newicom.dddd.utils.UUIDSupport.uuid

object InvoicingSaga {
  object InvoiceStatus extends Enumeration {
    type InvoiceStatus = Value
    val New, WaitingForPayment, Completed = Value
  }

  implicit object InvoicingSagaConfig extends SagaConfig[InvoicingSaga](invoicingOffice.streamName) {
    override def serializationHints = salesOffice.serializationHints ++ invoicingOffice.serializationHints
    def correlationIdResolver = {
      case rc: ReservationConfirmed => s"$uuid" // invoiceId
      case PaymentReceived(invoiceId, _, _, _) => invoiceId
    }
  }

}

import ecommerce.invoicing.InvoicingSaga.InvoiceStatus._

class InvoicingSaga(val pc: PassivationConfig, invoicingOffice: ActorPath) extends Saga {

  override def persistenceId = s"${InvoicingSagaConfig.name}Saga-$id"

  var status = New

  def receiveEvent = {
    case em @ EventMessage(_, e: ReservationConfirmed) if status == New =>
      raise(em)
    case em @ EventMessage(_, e: PaymentReceived) if status == WaitingForPayment =>
      raise(em)
  }

  def applyEvent = {
    case ReservationConfirmed(reservationId, customerId, totalAmountOpt) =>
      val totalAmount = totalAmountOpt.getOrElse(Money())
      deliverCommand(invoicingOffice, CreateInvoice(sagaId, reservationId, customerId, totalAmount, now()))
      status = WaitingForPayment

      // simulate payment receipt
      deliverCommand(invoicingOffice, ReceivePayment(sagaId, reservationId, totalAmount, paymentId = UUID.randomUUID().toString))

    case PaymentReceived(invoiceId, _, amount, paymentId) =>
      status = Completed
  }
}
