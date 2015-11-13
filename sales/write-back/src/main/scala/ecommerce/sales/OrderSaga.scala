package ecommerce.sales

import akka.actor.ActorPath
import ecommerce.invoicing.{OrderBilled, OrderBillingFailed}
import ecommerce.sales.OrderSaga._
import pl.newicom.dddd.actor.PassivationConfig
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

class OrderSaga(val pc: PassivationConfig, reservationOffice: ActorPath) extends Saga with StateHandling[OrderStatus] {

  override def persistenceId = s"${OrderSagaConfig.name}Saga-$id"

  val initialState = New

  def status = state

  def receiveEvent: ReceiveEvent = {
    case e: ReservationConfirmed if status.isNew =>
      ProcessEvent
    case e: OrderBilled if status.isNew =>
      ProcessEvent
    case e: OrderBillingFailed if status.isNew =>
      ProcessEvent
  }

  def stateMachine: StateMachine = {

    case New => {

      case OrderBilled(_, orderId, _, _) =>
        // close reservation
        deliverCommand(reservationOffice, CloseReservation(orderId))
        Completed

      case OrderBillingFailed(_, orderId) =>
        // cancel reservation
        deliverCommand(reservationOffice, CancelReservation(orderId))
        Failed
    }

  }

}
