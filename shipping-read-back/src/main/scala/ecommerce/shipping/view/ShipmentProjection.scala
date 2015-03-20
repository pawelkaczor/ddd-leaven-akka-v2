package ecommerce.shipping.view

import ecommerce.shipping.ShipmentCreated
import ecommerce.shipping.ShippingStatus.Waiting
import pl.newicom.dddd.messaging.event.DomainEventMessage
import pl.newicom.dddd.view.sql.Projection

import scala.slick.jdbc.JdbcBackend

class ShipmentProjection(dao: ShipmentDao) extends Projection {

  override def consume(eventMessage: DomainEventMessage)(implicit s: JdbcBackend.Session) {
    eventMessage.event match {
      case ShipmentCreated(id, orderId) =>
        dao.createIfNotExists(ShipmentView(id, orderId, Waiting))
    }
  }
}