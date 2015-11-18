package ecommerce.sales

import java.util.Date

import ecommerce.sales.Reservation.State
import ecommerce.sales.ReservationStatus._
import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.aggregate.{AggregateRoot, AggregateState, EntityId}
import pl.newicom.dddd.eventhandling.EventPublisher
import pl.newicom.dddd.office.LocalOfficeId.fromRemoteId

object Reservation {

  implicit val officeId = fromRemoteId[Reservation](SalesOfficeId)

  case class State(
      customerId: EntityId,
      status: ReservationStatus,
      items: List[ReservationItem],
      createDate: Date)
    extends AggregateState[State] {

    override def apply = {
      case ProductReserved(_, product, quantity) =>
        val newItems = items.find(item => item.productId == product.productId) match {
          case Some(orderLine) =>
            val index = items.indexOf(orderLine)
            items.updated(index, orderLine.increaseQuantity(quantity))
          case None =>
            ReservationItem(product, quantity) :: items
        }
        copy(items = newItems)

      case ReservationConfirmed(_, _, _) =>
        copy(status = Confirmed)

      case ReservationCanceled(_) =>
        copy(status = Canceled)

      case ReservationClosed(_) =>
        copy(status = Closed)
    }

    def totalAmount: Option[Money] = {
      items.foldLeft(Option.empty[Money]) {(mOpt, item) => mOpt.flatMap(m => item.product.price.map(_ + m)) }
    }
  }

}

abstract class Reservation(val pc: PassivationConfig) extends AggregateRoot[State, Reservation] {
  this: EventPublisher =>

  override val factory: AggregateRootFactory = {
    case ReservationCreated(_, customerId) =>
      State(customerId, Opened, items = List.empty, createDate = new Date)
  }

  override def handleCommand: Receive = {
    case CreateReservation(reservationId, clientId) =>
      if (initialized) {
        throw new RuntimeException(s"Reservation $reservationId already exists")
      } else {
        raise(ReservationCreated(reservationId, clientId))
      }

    case ReserveProduct(reservationId, product, quantity) =>
      if (state.status eq Closed) {
        throw new RuntimeException(s"Reservation $reservationId is closed")
      } else {
        raise(ProductReserved(reservationId, product, quantity))
      }

    case ConfirmReservation(reservationId) =>
      if ((state.status eq Closed) || (state.status eq Canceled)) {
        throw new RuntimeException(s"Reservation $reservationId is ${state.status}")
      } else {
        raise(ReservationConfirmed(reservationId, state.customerId, state.totalAmount))
      }

    case CancelReservation(reservationId) =>
      raise(ReservationCanceled(reservationId))

    case CloseReservation(reservationId) =>
      raise(ReservationClosed(reservationId))
  }
}
