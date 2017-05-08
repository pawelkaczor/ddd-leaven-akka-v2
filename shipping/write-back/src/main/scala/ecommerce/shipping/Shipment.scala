package ecommerce.shipping

import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.aggregate._
import pl.newicom.dddd.office.LocalOfficeId
import pl.newicom.dddd.office.LocalOfficeId.fromRemoteId

object Shipment extends AggregateRootSupport {

  sealed trait Shipping extends AggregateActions[Event, Shipping]

  implicit case object Uninitialized extends Shipping with Uninitialized[Shipping] {

    def actions: Actions =
      handleCommands {
        case CreateShipment(shipmentId, orderId) =>
          if (initialized) {
            error(s"Shipment $shipmentId already exists")
          } else {
            ShipmentCreated(shipmentId, orderId)
          }
      }
      .handleEvents {
        case ShipmentCreated(_, _) => Active
      }
  }

  case object Active extends Shipping {

    def actions: Actions = noActions

  }

  implicit val officeId: LocalOfficeId[Shipment] = fromRemoteId[Shipment](ShippingOfficeId)

}

import ecommerce.shipping.Shipment._

abstract class Shipment(val pc: PassivationConfig) extends AggregateRoot[Event, Shipping, Shipment] {
  this: ReplyConfig =>
}
