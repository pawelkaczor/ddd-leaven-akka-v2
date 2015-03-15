package ecommerce.shipping

import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.aggregate._
import pl.newicom.dddd.eventhandling.EventPublisher

object Shipment {

  case class State() extends AggregateState {
    override def apply = {
      case _ => this
    }
  }

}

import Shipment._

abstract class Shipment(override val pc: PassivationConfig) extends AggregateRoot[State] {
  this: EventPublisher =>

  override def persistenceId = s"${shippingOffice.name}-$id"

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
