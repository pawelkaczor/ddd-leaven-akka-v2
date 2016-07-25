package ecommerce.sales.view

import java.sql.Date

import ecommerce.sales.ReservationStatus._
import ecommerce.sales._
import org.joda.time.DateTime.now
import pl.newicom.dddd.messaging.event.OfficeEventMessage
import pl.newicom.dddd.view.sql.Projection
import pl.newicom.dddd.view.sql.Projection.ProjectionAction
import slick.dbio.DBIOAction
import slick.dbio.Effect.Write

import scala.concurrent.ExecutionContext

class ReservationProjection(dao: ReservationDao)(implicit ec: ExecutionContext) extends Projection {

  override def consume(eventMessage: OfficeEventMessage): ProjectionAction[Write] = {
    eventMessage.event match {

      case ReservationCreated(id, clientId) =>
        val newView = ReservationView(id, clientId, Opened, new Date(now().getMillis))
        dao.createOrUpdate(newView)

      case ReservationConfirmed(id, clientId, _) =>
          dao.updateStatus(id, Confirmed)

      case ReservationCanceled(id) =>
        dao.updateStatus(id, Canceled)

      case ReservationClosed(id) =>
        dao.updateStatus(id, Closed)

      case ProductReserved(id, product, quantity) =>
        // TODO handle
        DBIOAction.successful(())
    }
  }
}