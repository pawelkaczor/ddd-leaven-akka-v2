package ecommerce.sales.view

import java.sql.Date

import com.typesafe.config.{Config, ConfigFactory}
import ecommerce.sales.ReservationCreated
import ecommerce.sales.ReservationConfirmed
import ecommerce.sales.ReservationStatus.{Confirmed, Opened}
import org.joda.time.DateTime.now
import org.scalatest._
import pl.newicom.dddd.messaging.event.{AggregateSnapshotId, DomainEventMessage}
import scala.concurrent.ExecutionContext.Implicits.global

class ReservationProjectionSpec extends WordSpecLike with Matchers with ViewTestSupport {

  override def config: Config = ConfigFactory.load()

  val dao = new ReservationDao
  val projection = new ReservationProjection(dao)

  "ReservationProjection" should {
    "consume ReservationCreated event" in {
      // When
      projection.consume(ReservationCreated("reservation-1", "client-1")).run()

      // Then
      val reservation = dao.byId("reservation-1").result
      assert(reservation.map(_.status) == Some(Opened))
    }
  }

  "ReservationProjection" should {
    "consume ReservationConfirmed event" in {
      // Given

      dao.createOrUpdate(ReservationView("reservation-1", "client-1", Opened, new Date(now.getMillis))).run()

      // When
      projection.consume(ReservationConfirmed("reservation-1", "client-1", None)).run()

      // Then
      val reservation = dao.byId("reservation-1").result
      assert(reservation.map(_.status) == Some(Confirmed))
    }
  }

  override def ensureSchemaDropped = dao.ensureSchemaDropped

  override def ensureSchemaCreated = dao.ensureSchemaCreated

  implicit def toEventMessage(event: ReservationCreated): DomainEventMessage = DomainEventMessage(AggregateSnapshotId(event.reservationId), event)
  implicit def toEventMessage(event: ReservationConfirmed): DomainEventMessage = DomainEventMessage(AggregateSnapshotId(event.reservationId), event)

}
