package ecommerce.headquarters.processes

import java.util.UUID
import com.github.nscala_time.time.Imports._
import ecommerce.invoicing.{CancelInvoice, CreateInvoice, OrderBilled, OrderBillingFailed, PaymentExpired}
import ecommerce.headquarters.processes.OrderProcessManager.OrderStatus
import ecommerce.sales.{CancelReservation, CloseReservation, Money, ReservationConfirmed}
import ecommerce.shipping.CreateShipment
import org.joda.time.DateTime._
import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.office.Office
import pl.newicom.dddd.process._
import pl.newicom.dddd.saga.SagaConfig

object OrderProcessManager extends SagaSupport {

  sealed trait OrderStatus extends SagaState[OrderStatus] {
    def isNew = false
  }
  case object New extends OrderStatus {
    override def isNew: Boolean = true
  }
  case object WaitingForPayment extends OrderStatus
  case object Completed         extends OrderStatus
  case object Failed            extends OrderStatus

  implicit object OrderProcessConfig extends SagaConfig[OrderProcessManager]("order", "Headquarters") {
    def correlationIdResolver = {
      case ReservationConfirmed(reservationId, _, _) => reservationId // orderId
      case OrderBilled(_, orderId, _, _) => orderId
      case OrderBillingFailed(_, orderId) => orderId
      case PaymentExpired(_, orderId) => orderId
    }
  }

}

import ecommerce.headquarters.processes.OrderProcessManager._

class OrderProcessManager(
                           reservation: Office, invoicing: Office, shipping: Office,
                           val pc: PassivationConfig,
                           override val schedulingOffice: Option[Office])
  extends ProcessManager[OrderStatus] {

  val officeId = OrderProcessConfig

  override def receiveEvent =
    super.receiveEvent.orElse {
      case e: PaymentExpired if state != WaitingForPayment => DropEvent
    }

  startWhen {

    case rc: ReservationConfirmed => New

  } andThen {

    case New => {

      case ReservationConfirmed(reservationId, customerId, totalAmountOpt) =>
        val totalAmount = totalAmountOpt.getOrElse(Money())

        invoicing !! CreateInvoice(sagaId, reservationId, customerId, totalAmount, now())

        schedule (PaymentExpired(sagaId, reservationId)) in 3.minutes

        WaitingForPayment

    }

    case WaitingForPayment => {

      case OrderBilled(_, orderId, _, _) =>

        reservation !! CloseReservation(orderId)

        shipping !! CreateShipment(UUID.randomUUID().toString, orderId)

        Completed

      case PaymentExpired(invoiceId, orderId) =>
        log.debug("Payment expired for order '{}'.", orderId)

        invoicing !! CancelInvoice(invoiceId, orderId)

      case OrderBillingFailed(_, orderId) =>

        reservation !! CancelReservation(orderId)

        Failed
    }

  }

}
