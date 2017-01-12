package ecommerce.shipping

import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.aggregate._
import pl.newicom.dddd.eventhandling.EventPublisher
import pl.newicom.dddd.office.LocalOfficeId
import pl.newicom.dddd.office.LocalOfficeId.fromRemoteId

object Shipment extends AggregateRootSupport {

  implicit val officeId: LocalOfficeId[Shipment] =
    fromRemoteId[Shipment](ShippingOfficeId)

  sealed trait State extends AggregateState[State]

  implicit case object Uninitialized extends State with Uninitialized[State] {
    def apply: StateMachine = {
      case ShipmentCreated(_, _) => Active
    }
  }

  case object Active extends State {
    def apply: StateMachine = PartialFunction.empty
  }

}

import ecommerce.shipping.Shipment._

abstract class Shipment(val pc: PassivationConfig) extends AggregateRoot[State, Shipment] {
  this: EventPublisher =>

  def handleCommand: Receive = {
    case CreateShipment(shipmentId, orderId) =>
      if (initialized) {
        throw new RuntimeException(s"Shipment $shipmentId already exists")
      } else {
        raise(ShipmentCreated(shipmentId, orderId))
      }
  }
}
