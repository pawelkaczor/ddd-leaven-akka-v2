package ecommerce.shipping

import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.aggregate._
import pl.newicom.dddd.eventhandling.EventPublisher
import pl.newicom.dddd.office.LocalOfficeId
import pl.newicom.dddd.office.LocalOfficeId.fromRemoteId
import scala.PartialFunction.empty

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
    def apply: StateMachine = empty
  }

}

import ecommerce.shipping.Shipment._

abstract class Shipment(val pc: PassivationConfig) extends AggregateRoot[Event, State, Shipment] {
  this: EventPublisher =>

  def handleCommand: HandleCommand = {
    case CreateShipment(shipmentId, orderId) =>
      if (initialized) {
        sys.error(s"Shipment $shipmentId already exists")
      } else {
        ShipmentCreated(shipmentId, orderId)
      }
  }
}
