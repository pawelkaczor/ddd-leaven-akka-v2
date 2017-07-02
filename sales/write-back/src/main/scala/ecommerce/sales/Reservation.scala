package ecommerce.sales

import java.util.Date

import pl.newicom.dddd.aggregate._
import pl.newicom.dddd.office.LocalOfficeId
import pl.newicom.dddd.office.LocalOfficeId.fromRemoteId

object Reservation extends AggregateRootSupport {

  sealed trait ReservationActions extends AggregateActions[Event, ReservationActions, Config] {

    def canceledOrClosed: Actions =
      handleCommand {
        case CloseReservation(reservationId) =>
          ReservationClosed(reservationId)

        case CancelReservation(reservationId) =>
          ReservationCanceled(reservationId)
      }
      .handleEvent {
        case ReservationCanceled(_) => Canceled
        case ReservationClosed(_) => Closed
      }

  }

  implicit case object Uninitialized extends ReservationActions with Uninitialized[ReservationActions] {

    def actions: Actions =
      handleCommand {
        case CreateReservation(reservationId, clientId) =>
          ReservationCreated(reservationId, clientId)
      }
      .handleEvent {
        case ReservationCreated(_, customerId) =>
          Opened(customerId, items = List.empty, createDate = new Date)
      }

  }

  case class Opened(customerId: EntityId, items: List[ReservationItem], createDate: Date) extends ReservationActions {

    def actions: Actions =
      handleCommand {
        case ReserveProduct(reservationId, product, quantity) =>
          ProductReserved(reservationId, product, quantity)

        case ConfirmReservation(reservationId) =>
          ReservationConfirmed(reservationId, customerId, totalAmount)
      }
      .handleEvent {
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
      .orElse(canceledOrClosed)

    def totalAmount: Option[Money] = {
      items.foldLeft(Money()) {
        (m, item) => item.product.price.getOrElse(Money()) + m
      } match {
        case Money(0d, _) => None
        case m => Some(m)
      }
    }

  }

  case object Confirmed extends ReservationActions {
    def actions: Actions = canceledOrClosed
  }

  case object Canceled extends ReservationActions {
    def actions: Actions = canceledOrClosed
  }

  case object Closed extends ReservationActions {
    def actions: Actions = noActions
  }

  implicit val officeId: LocalOfficeId[Reservation] = fromRemoteId[Reservation](ReservationOfficeId)

}

import ecommerce.sales.Reservation._

abstract class Reservation(val config: Config) extends AggregateRoot[Event, ReservationActions, Reservation] with ConfigClass[Config]