package ecommerce.sales

import java.util.Date

import pl.newicom.dddd.actor.{Config, ConfigClass}
import pl.newicom.dddd.aggregate._
import pl.newicom.dddd.office.LocalOfficeId
import pl.newicom.dddd.office.LocalOfficeId.fromRemoteId

object ReservationAggregateRoot extends AggregateRootSupport {

  sealed trait Reservation extends Behavior[Event, Reservation, Config] {

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

  implicit case object Uninitialized extends Reservation with Uninitialized[Reservation] {

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

  case class Opened(customerId: EntityId, items: List[ReservationItem], createDate: Date) extends Reservation {

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

    def totalAmount: Money =
      items.foldLeft(Money()) {
        (m, item) => item.product.price + m
      }

  }

  case object Confirmed extends Reservation {
    def actions: Actions = canceledOrClosed
  }

  case object Canceled extends Reservation {
    def actions: Actions = canceledOrClosed
  }

  case object Closed extends Reservation {
    def actions: Actions = noActions
  }

  implicit val officeId: LocalOfficeId[ReservationAggregateRoot] = fromRemoteId[ReservationAggregateRoot](ReservationOfficeId)

}

import ecommerce.sales.ReservationAggregateRoot._

class ReservationAggregateRoot(val config: Config) extends AggregateRoot[Event, Reservation, ReservationAggregateRoot] with ConfigClass[Config]