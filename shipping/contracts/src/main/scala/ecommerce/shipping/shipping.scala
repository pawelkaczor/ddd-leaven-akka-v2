package ecommerce.shipping

import pl.newicom.dddd.aggregate
import pl.newicom.dddd.aggregate._

//
// Commands
//

sealed trait Command extends aggregate.Command {
  def shipmentId: ShipmentId
  override def aggregateId: ShipmentId = shipmentId
}

case class CreateShipment(shipmentId: ShipmentId, orderId: EntityId) extends Command

//
// Events
//
sealed trait Event

case class ShipmentCreated(shipmentId: ShipmentId, orderId: EntityId) extends Event

case class GoodsDelivered(shipmentId: ShipmentId, orderId: EntityId) extends Event

//
// Value Objects
//
object ShippingStatus extends Enumeration {
  type ShippingStatus = Value

  val Waiting, Sent, Delivered = Value

}