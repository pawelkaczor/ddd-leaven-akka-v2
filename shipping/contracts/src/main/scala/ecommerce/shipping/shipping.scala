package ecommerce.shipping

import pl.newicom.dddd.aggregate
import pl.newicom.dddd.aggregate._

//
// Commands
//

sealed trait Command extends aggregate.Command {
  def shipmentId: EntityId
  override def aggregateId = shipmentId
}

case class CreateShipment(shipmentId: EntityId, orderId: EntityId) extends Command

//
// Events
//
sealed trait Event

case class ShipmentCreated(shipmentId: EntityId, orderId: EntityId) extends Event

case class GoodsDelivered(shipmentId: EntityId, orderId: EntityId) extends Event

//
// Value Objects
//
object ShippingStatus extends Enumeration {
  type ShippingStatus = Value

  val Waiting, Sent, Delivered = Value

}