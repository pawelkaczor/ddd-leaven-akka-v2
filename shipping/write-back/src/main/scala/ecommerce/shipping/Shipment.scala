package ecommerce.shipping

import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.aggregate._
import pl.newicom.dddd.eventhandling.EventPublisher
import pl.newicom.dddd.office.LocalOfficeId.fromRemoteId

object Shipment {

  implicit val officeId = fromRemoteId[Shipment](ShippingOfficeId)

  case class State() extends AggregateState[State] {
    override def apply = {
      case _ => this
    }
  }

}

import ecommerce.shipping.Shipment._

abstract class Shipment(val pc: PassivationConfig) extends AggregateRoot[State, Shipment] {
  this: EventPublisher =>

  override val factory: AggregateRootFactory = {
    case ShipmentCreated(_, _) =>
      State()
  }

  override def handleCommand: Receive = {
    case CreateShipment(shipmentId, orderId) =>
      if (initialized) {
        throw new RuntimeException(s"Shipment $shipmentId already exists")
      } else {
        raise(ShipmentCreated(shipmentId, orderId))
      }
  }
}
