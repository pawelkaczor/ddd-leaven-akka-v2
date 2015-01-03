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

case class CreateReservation(reservationId: EntityId, clientId: EntityId) extends Command
case class ReserveProduct(reservationId: EntityId, product: Product, quantity: Int) extends Command
case class ConfirmReservation(reservationId: EntityId) extends Command
case class CloseReservation(reservationId: EntityId) extends Command


//
// Events
//

case class ReservationCreated(reservationId: EntityId, clientId: EntityId) extends DomainEvent
case class ProductReserved(reservationId: EntityId, product: Product, quantity: Int) extends DomainEvent
case class ReservationConfirmed(reservationId: EntityId, clientId: EntityId) extends DomainEvent
case class ReservationClosed(reservationId: EntityId) extends DomainEvent

// Value Objects

object ReservationStatus extends Enumeration {
  type ReservationStatus = Value
  val Opened, Confirmed, Closed = Value
}

case class ReservationItem(product: Product, quantity: Int) extends BusinessEntity {

  override val id: String = UUID.randomUUID().toString

  def increaseQuantity(addedQuantity: Int) = copy(quantity = this.quantity + addedQuantity)

  def productId = product.productId
}