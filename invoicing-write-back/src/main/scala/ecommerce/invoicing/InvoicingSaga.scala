package ecommerce.invoicing

import java.util.UUID

import akka.actor.ActorPath
import ecommerce.invoicing.Invoice.{CreateInvoice, PaymentReceived}
import ecommerce.sales.{ReservationConfirmed, salesOffice}
import org.joda.time.DateTime.now
import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.messaging.event.EventMessage
import pl.newicom.dddd.process.{Saga, SagaConfig}

object InvoicingSaga {
  object InvoiceStatus extends Enumeration {
    type InvoiceStatus = Value
    val New, WaitingForPayment, Completed = Value
  }

  implicit object InvoicingSagaConfig extends SagaConfig[InvoicingSaga]("Sales") {

    override def serializationHints = salesOffice.serializationHints ++ invoicingOffice.serializationHints

    def correlationIdResolver = {
      case rc: ReservationConfirmed => UUID.randomUUID().toString // invoiceId
      case PaymentReceived(invoiceId, _, _) => invoiceId
    }
  }

}

import ecommerce.invoicing.InvoicingSaga.InvoiceStatus._

class InvoicingSaga(val pc: PassivationConfig, invoiceOffice: ActorPath) extends Saga {

  var status = New

  def receiveEvent = {
    case em @ EventMessage(_, e: ReservationConfirmed) if status == New =>
      raise(em)
    case em @ EventMessage(_, e: PaymentReceived) if status == WaitingForPayment =>
      raise(em)
  }

  def applyEvent = {
    case ReservationConfirmed(reservationId, customerId, totalAmount) =>
      if (totalAmount.isDefined) {
        deliverCommand(invoiceOffice, CreateInvoice(sagaId, reservationId, customerId, totalAmount.get, now()))
        status = WaitingForPayment
      } else {
        status = Completed
      }
    case PaymentReceived(invoiceId, amount, paymentId) =>
      status = Completed
  }
}
