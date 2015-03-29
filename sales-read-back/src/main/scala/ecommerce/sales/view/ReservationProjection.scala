package ecommerce.sales.view

import java.sql.Date

import ecommerce.sales.ReservationStatus._
import ecommerce.sales.{ReservationCanceled, ReservationClosed, ReservationConfirmed, ReservationCreated}
import org.joda.time.DateTime.now
import pl.newicom.dddd.messaging.event.DomainEventMessage
import pl.newicom.dddd.view.sql.Projection

import scala.slick.jdbc.JdbcBackend

class ReservationProjection(dao: ReservationDao) extends Projection {

  override def consume(eventMessage: DomainEventMessage)(implicit s: JdbcBackend.Session) {
    eventMessage.event match {
      case ReservationCreated(id, clientId) =>
        dao.createIfNotExists(ReservationView(id, clientId, Opened, new Date(now().getMillis)))
      case ReservationConfirmed(id, clientId, _) =>
        dao.byId(id).foreach { old => dao.update(old.copy(status = Confirmed)) }
      case ReservationCanceled(id) =>
        dao.byId(id).foreach { old => dao.update(old.copy(status = Canceled)) }
      case ReservationClosed(id) =>
        dao.byId(id).foreach { old => dao.update(old.copy(status = Closed)) }
    }
  }
}