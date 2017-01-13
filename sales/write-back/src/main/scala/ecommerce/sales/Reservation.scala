package ecommerce.sales

import java.util.Date

import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.aggregate._
import pl.newicom.dddd.eventhandling.EventPublisher
import pl.newicom.dddd.office.LocalOfficeId
import pl.newicom.dddd.office.LocalOfficeId.fromRemoteId

import scala.PartialFunction.empty

object Reservation extends AggregateRootSupport {

  implicit val officeId: LocalOfficeId[Reservation] =
    fromRemoteId[Reservation](ReservationOfficeId)

  sealed trait State extends AggregateState[State] {
    def common: StateMachine = {
      case ReservationCanceled(_) => Canceled
      case ReservationClosed(_) => Closed
    }
  }

  implicit case object Uninitialized extends State with Uninitialized[State] {
    def apply: StateMachine = {
      case ReservationCreated(_, customerId) =>
        Opened(customerId, items = List.empty, createDate = new Date)
    }
  }

  case class Opened(customerId: EntityId, items: List[ReservationItem], createDate: Date) extends State {

    def apply: StateMachine = common orElse {
      case ProductReserved(_, product, quantity) =>
        val newItems = items.find(item => item.productId == product.id) match {
          case Some(orderLine) =>
            val index = items.indexOf(orderLine)
            items.updated(index, orderLine.increaseQuantity(quantity))
          case None =>
            ReservationItem(product, quantity) :: items
        }
        copy(items = newItems)

      case _: ReservationConfirmed =>
        Confirmed
    }

    def totalAmount: Option[Money] = {
      items.foldLeft(Option.empty[Money]) {(mOpt, item) => mOpt.flatMap(m => item.product.price.map(_ + m)) }
    }
  }

  case object Confirmed extends State {
    def apply: StateMachine = common
  }

  case object Canceled extends State {
    def apply: StateMachine = common
  }

  case object Closed extends State {
    def apply: StateMachine = empty
  }

}

import ecommerce.sales.Reservation._

abstract class Reservation(val pc: PassivationConfig) extends AggregateRoot[Event, State, Reservation] {
  this: EventPublisher =>

  def handleCommand: HandleCommand = state match {

    case Uninitialized => {
      case CreateReservation(reservationId, clientId) =>
        ReservationCreated(reservationId, clientId)
    }

    case state @ Opened(customerId, _, _) => common orElse {

      case ReserveProduct(reservationId, product, quantity) =>
        ProductReserved(reservationId, product, quantity)

      case ConfirmReservation(reservationId) =>
        ReservationConfirmed(reservationId, customerId, state.totalAmount)

    }

    case Confirmed => common

    case Canceled => common

    case Closed => empty
  }

  def common: HandleCommand = {
    case CloseReservation(reservationId) =>
      ReservationClosed(reservationId)

    case CancelReservation(reservationId) =>
      ReservationCanceled(reservationId)
  }


}
