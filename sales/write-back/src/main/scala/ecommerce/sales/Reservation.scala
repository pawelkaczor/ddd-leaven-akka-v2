package ecommerce.sales

import java.util.Date

import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.aggregate._
import pl.newicom.dddd.eventhandling.EventPublisher
import pl.newicom.dddd.office.LocalOfficeId
import pl.newicom.dddd.office.LocalOfficeId.fromRemoteId

import scala.PartialFunction.empty

object Reservation extends AggregateRootSupport {

  sealed trait ReservationBehaviour extends AggregateBehaviour[Event, ReservationBehaviour] {

    def canceledOrClosed: StateMachine = {
      case ReservationCanceled(_) => Canceled
      case ReservationClosed(_) => Closed
    }

    def closeOrCancel: HandleCommand = {
      case CloseReservation(reservationId) =>
        ReservationClosed(reservationId)

      case CancelReservation(reservationId) =>
        ReservationCanceled(reservationId)
    }

  }

  implicit case object Uninitialized extends ReservationBehaviour with Uninitialized[ReservationBehaviour] {

    def handleCommand = {
      case CreateReservation(reservationId, clientId) =>
        ReservationCreated(reservationId, clientId)
    }

    def apply = {
      case ReservationCreated(_, customerId) =>
        Opened(customerId, items = List.empty, createDate = new Date)
    }
  }

  case class Opened(customerId: EntityId, items: List[ReservationItem], createDate: Date) extends ReservationBehaviour {

    def handleCommand = closeOrCancel orElse {
      case ReserveProduct(reservationId, product, quantity) =>
        ProductReserved(reservationId, product, quantity)

      case ConfirmReservation(reservationId) =>
        ReservationConfirmed(reservationId, customerId, totalAmount)
    }

    def apply = canceledOrClosed orElse {
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

  case object Confirmed extends ReservationBehaviour {
    def handleCommand = closeOrCancel
    def apply = canceledOrClosed
  }

  case object Canceled extends ReservationBehaviour {
    def handleCommand = closeOrCancel
    def apply = canceledOrClosed
  }

  case object Closed extends ReservationBehaviour {
    def handleCommand = empty
    def apply = empty
  }

  implicit val officeId: LocalOfficeId[Reservation] = fromRemoteId[Reservation](ReservationOfficeId)

}

import ecommerce.sales.Reservation._

abstract class Reservation(val pc: PassivationConfig) extends AggregateRoot[Event, ReservationBehaviour, Reservation] {
  this: EventPublisher =>

}
