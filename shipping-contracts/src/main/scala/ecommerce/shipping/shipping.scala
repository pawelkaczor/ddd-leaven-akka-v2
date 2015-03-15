package ecommerce.shipping

import pl.newicom.dddd.aggregate._

trait ShippingOffice

//
// Commands
//
case class CreateShipment(shipmentId: EntityId, orderId: EntityId)

//
// Events
//
case class ShipmentCreated(shipmentId: EntityId, orderId: EntityId)

//
// Value Objects
//
object ShippingStatus extends Enumeration {
  type ShippingStatus = Value

  val Waiting, Sent, Delivered = Value

}