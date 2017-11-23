package ecommerce.sales

import java.util.UUID

import pl.newicom.dddd.aggregate
import pl.newicom.dddd.aggregate._

//
// Commands
//

sealed trait Command extends aggregate.Command {
  def reservationId: ReservationId
  override def aggregateId = reservationId
}

case class CreateReservation(reservationId: ReservationId, customerId: EntityId) extends Command
case class ReserveProduct(reservationId: ReservationId, product: Product, quantity: Int) extends Command
case class ConfirmReservation(reservationId: ReservationId) extends Command
case class CancelReservation(reservationId: ReservationId) extends Command
case class CloseReservation(reservationId: ReservationId) extends Command


//
// Events
//
sealed trait Event

case class ReservationCreated(reservationId: ReservationId, customerId: EntityId) extends Event
case class ProductReserved(reservationId: ReservationId, product: Product, quantity: Int) extends Event
case class ReservationConfirmed(reservationId: ReservationId, customerId: EntityId, totalAmount: Option[Money]) extends Event
case class ReservationCanceled(reservationId: ReservationId) extends Event
case class ReservationClosed(reservationId: ReservationId) extends Event

// Value Objects

object ReservationStatus extends Enumeration {
  type ReservationStatus = Value
  val Opened, Confirmed, Canceled, Closed = Value
}

case class ReservationItem(product: Product, quantity: Int) {

  val id: String = UUID.randomUUID().toString

  def increaseQuantity(addedQuantity: Int) = copy(quantity = this.quantity + addedQuantity)

  def productId = product.id
}