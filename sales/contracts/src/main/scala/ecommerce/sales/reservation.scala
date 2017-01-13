package ecommerce.sales

import java.util.UUID

import pl.newicom.dddd.aggregate
import pl.newicom.dddd.aggregate._

//
// Commands
//

sealed trait Command extends aggregate.Command {
  def reservationId: EntityId
  override def aggregateId = reservationId
}

case class CreateReservation(reservationId: EntityId, customerId: EntityId) extends Command
case class ReserveProduct(reservationId: EntityId, product: Product, quantity: Int) extends Command
case class ConfirmReservation(reservationId: EntityId) extends Command
case class CancelReservation(reservationId: EntityId) extends Command
case class CloseReservation(reservationId: EntityId) extends Command


//
// Events
//
sealed trait Event

case class ReservationCreated(reservationId: EntityId, customerId: EntityId) extends Event
case class ProductReserved(reservationId: EntityId, product: Product, quantity: Int) extends Event
case class ReservationConfirmed(reservationId: EntityId, customerId: EntityId, totalAmount: Option[Money]) extends Event
case class ReservationCanceled(reservationId: EntityId) extends Event
case class ReservationClosed(reservationId: EntityId) extends Event

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