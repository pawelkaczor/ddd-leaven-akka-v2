package ecommerce.sales

import akka.actor.ActorPath
import ecommerce.invoicing.{OrderBilled, OrderBillingFailed}
import ecommerce.sales.OrderSaga.OrderSagaConfig
import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.messaging.event.EventMessage
import pl.newicom.dddd.process.{Saga, SagaConfig}
import pl.newicom.dddd.utils.UUIDSupport.uuid

object OrderStatus extends Enumeration {
  type InvoiceStatus = Value
  val New, Completed, Failed = Value
}

import OrderStatus._

object OrderSaga {

  implicit object OrderSagaConfig extends SagaConfig[OrderSaga]("sales") {
    def correlationIdResolver = {
      case rc: ReservationConfirmed => s"$uuid" // orderId
      case OrderBilled(_, orderId, _, _) => orderId
      case OrderBillingFailed(_, orderId) => orderId
    }
  }

}

class OrderSaga(val pc: PassivationConfig, reservationOffice: ActorPath) extends Saga {

  override def persistenceId = s"${OrderSagaConfig.name}Saga-$id"

  var status = New

  def receiveEvent = {
    case em @ EventMessage(_, e: ReservationConfirmed) if status == New =>
      raise(em)
    case em @ EventMessage(_, e: OrderBilled) if status == New =>
      raise(em)
    case em @ EventMessage(_, e: OrderBillingFailed) if status == New =>
      raise(em)
  }

  def applyEvent = {
    case e: ReservationConfirmed =>
       // do nothing

    case OrderBilled(_, orderId, _, _) =>
      // close reservation
      deliverCommand(reservationOffice, CloseReservation(orderId))
      status = Completed

    case OrderBillingFailed(_, orderId) =>
      // cancel reservation
      deliverCommand(reservationOffice, CancelReservation(orderId))
      status = Failed
  }
}
