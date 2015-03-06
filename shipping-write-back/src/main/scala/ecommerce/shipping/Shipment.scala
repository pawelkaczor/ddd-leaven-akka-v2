package ecommerce.shipping

import ecommerce.sales.Shipment._
import ecommerce.shipping.ShipmentCreated
import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.aggregate._
import pl.newicom.dddd.eventhandling.EventPublisher

object Shipment {

  def persistenceId(aggregateId: EntityId) = "Shipment-" + aggregateId

  case class State() extends AggregateState {
    override def apply = {
      case _ => this
    }
  }

}

abstract class Shipment(override val pc: PassivationConfig) extends AggregateRoot[State] {
  this: EventPublisher =>

  override def persistenceId = Shipment.persistenceId(id)

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
