package ecommerce.shipping

import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.aggregate._
import pl.newicom.dddd.eventhandling.EventPublisher
import pl.newicom.dddd.office.LocalOfficeId
import pl.newicom.dddd.office.LocalOfficeId.fromRemoteId
import scala.PartialFunction.empty

object Shipment extends AggregateRootSupport {

  sealed trait Shipping extends AggregateBehaviour[Event, Shipping]

  implicit case object Uninitialized extends Shipping with Uninitialized[Shipping] {

    def handleCommand = {
      case CreateShipment(shipmentId, orderId) =>
        if (initialized) {
          sys.error(s"Shipment $shipmentId already exists")
        } else {
          ShipmentCreated(shipmentId, orderId)
        }
    }

    def apply = {
      case ShipmentCreated(_, _) => Active
    }
  }

  case object Active extends Shipping {

    def handleCommand = empty

    def apply = empty
  }

  implicit val officeId: LocalOfficeId[Shipment] = fromRemoteId[Shipment](ShippingOfficeId)

}

import ecommerce.shipping.Shipment._

abstract class Shipment(val pc: PassivationConfig) extends AggregateRoot[Event, Shipping, Shipment] {
  this: EventPublisher =>

}
