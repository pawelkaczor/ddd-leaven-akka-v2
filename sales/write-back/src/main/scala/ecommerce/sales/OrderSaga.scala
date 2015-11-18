package ecommerce.sales

import akka.actor.ActorPath
import ecommerce.invoicing.{OrderBilled, OrderBillingFailed}
import ecommerce.sales.OrderSaga._
import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.office.SagaConfig
import pl.newicom.dddd.process._
import pl.newicom.dddd.utils.UUIDSupport.uuid

object OrderSaga {

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
      case rc: ReservationConfirmed => s"$uuid" // orderId
      case OrderBilled(_, orderId, _, _) => orderId
      case OrderBillingFailed(_, orderId) => orderId
    }
  }

}

class OrderSaga(val pc: PassivationConfig,
                reservationOffice: ActorPath) extends ProcessManager[OrderStatus] {

  val officeId = OrderSagaConfig

  startWhen {

    case _:OrderBilled | _:OrderBillingFailed => New

  } andThen {

    case New => {

      case OrderBilled(_, orderId, _, _) =>
        deliverCommand(reservationOffice, CloseReservation(orderId)) // close reservation
        goto(Completed)

      case OrderBillingFailed(_, orderId) =>
        deliverCommand(reservationOffice, CancelReservation(orderId)) // cancel reservation
        goto(Failed)
    }

  }

}
