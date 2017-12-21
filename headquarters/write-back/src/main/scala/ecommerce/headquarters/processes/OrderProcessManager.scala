package ecommerce.headquarters.processes

import com.github.nscala_time.time.Imports._
import ecommerce.headquarters.app.HeadquartersConfiguration.HQDepartment
import ecommerce.headquarters.processes.OrderProcessManager.OrderStatus
import ecommerce.invoicing.{CancelInvoice, CreateInvoice, OrderBilled, OrderBillingFailed, PaymentExpired, _}
import ecommerce.sales._
import ecommerce.shipping.{CreateShipment, ShipmentId}
import org.joda.time.DateTime._
import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.process._
import pl.newicom.dddd.saga.{BusinessProcessId, ProcessConfig}

object OrderProcessManager extends SagaSupport {

  val ProcessDomain, ProcessId = "order"

  sealed trait OrderStatus extends SagaState[OrderStatus] {
    def isNew = false
  }
  case object New extends OrderStatus {
    override def isNew: Boolean = true
  }
  case object WaitingForPayment  extends OrderStatus
  case object DeliveryInProgress extends OrderStatus
  case object Completed          extends OrderStatus
  case object Failed             extends OrderStatus

  implicit object OrderProcessConfig extends ProcessConfig[OrderProcessManager](BusinessProcessId(ProcessDomain, ProcessId, HQDepartment)) {
    def correlationIdResolver = {
      case ReservationConfirmed(reservationId, _, _) => reservationId.value // orderId
      case OrderBilled(_, orderId, _, _)             => orderId
      case OrderBillingFailed(_, orderId)            => orderId
      case PaymentExpired(_, orderId)                => orderId
    }
  }

}

import ecommerce.headquarters.processes.OrderProcessManager._

class OrderProcessManager(val pc: PassivationConfig, shipmentIdGen: () => ShipmentId) extends ProcessManager[OrderStatus] {

  val officeId = OrderProcessConfig

  startWhen {

    case _: ReservationConfirmed => New

  } andThen {

    case New => {

      case ReservationConfirmed(reservationId, customerId, totalAmountOpt) =>
        WaitingForPayment {
          ⟶(CreateInvoice(new InvoiceId(sagaId), reservationId.value, customerId, totalAmountOpt.getOrElse(Money()), now()))

          ⟵(PaymentExpired(new InvoiceId(sagaId), reservationId.value)) in 3.minutes
        }

    }

    case WaitingForPayment => {

      case PaymentExpired(invoiceId, orderId) =>
        ⟶(CancelInvoice(invoiceId, orderId))

      case OrderBilled(_, orderId, _, _) =>
        DeliveryInProgress {
          ⟶(CloseReservation(new ReservationId(orderId)))

          ⟶(CreateShipment(shipmentIdGen(), orderId))
        }

      case OrderBillingFailed(_, orderId) =>
        Failed {
          ⟶(CancelReservation(new ReservationId(orderId)))
        }
    }

  }

}
