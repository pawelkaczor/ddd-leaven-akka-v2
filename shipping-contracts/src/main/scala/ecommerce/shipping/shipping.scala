package ecommerce.shipping

import org.json4s.ext.EnumSerializer
import org.json4s.{FullTypeHints, TypeHints}
import pl.newicom.dddd.aggregate._

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

object json {
  implicit val typeHints: TypeHints = ShippingCommands + ShippingEvents
  implicit val formats: EnumSerializer[ShippingStatus.type] = new EnumSerializer(ShippingStatus)

  object ShippingCommands extends FullTypeHints(
    List(
      classOf[CreateShipment]
    ))

  object ShippingEvents extends FullTypeHints(
    List(
      classOf[ShipmentCreated]
    ))

}