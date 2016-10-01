package ecommerce.sales

import ecommerce.invoicing.{OrderBilled, OrderBillingFailed}
import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.office.Office
import pl.newicom.dddd.process._
import pl.newicom.dddd.saga.SagaConfig

object OrderSaga extends SagaSupport {

  sealed trait OrderStatus extends SagaState[OrderStatus] {
    def isNew = false
  }
  case object New extends OrderStatus {
    override def isNew: Boolean = true
  }

  case object Completed extends OrderStatus
  case object Failed extends OrderStatus

  implicit object OrderSagaConfig extends SagaConfig[OrderSaga]("sales") {
    def correlationIdResolver = {
      case ReservationConfirmed(reservationId, _, _) => reservationId // orderId
      case OrderBilled(_, orderId, _, _) => orderId
      case OrderBillingFailed(_, orderId) => orderId
    }
  }

}

import ecommerce.sales.OrderSaga._

class OrderSaga(val pc: PassivationConfig, reservationOffice: Office) extends ProcessManager[OrderStatus] {

  val officeId = OrderSagaConfig

  startWhen {

    case rc: ReservationConfirmed => New

  } andThen {

    case New => {

      case ReservationConfirmed =>

      case OrderBilled(_, orderId, _, _) =>
        reservationOffice !! CloseReservation(orderId)

        Completed

      case OrderBillingFailed(_, orderId) =>
        reservationOffice !! CancelReservation(orderId)

        Failed
    }

  }

}
