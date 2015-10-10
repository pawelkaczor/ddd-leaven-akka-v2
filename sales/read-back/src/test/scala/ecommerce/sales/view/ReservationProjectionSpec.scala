package ecommerce.sales.view

import java.sql.Date

import com.typesafe.config.{Config, ConfigFactory}
import ecommerce.sales.ReservationCreated
import ecommerce.sales.ReservationConfirmed
import ecommerce.sales.ReservationStatus.{Confirmed, Opened}
import org.joda.time.DateTime.now
import org.scalatest._
import pl.newicom.dddd.messaging.event.{AggregateSnapshotId, DomainEventMessage}

import scala.slick.jdbc.JdbcBackend

class ReservationProjectionSpec extends WordSpecLike with Matchers with ViewTestSupport {

  override def config: Config = ConfigFactory.load()

  val dao = new ReservationDao
  val projection = new ReservationProjection(dao)

  "ReservationProjection" should {
    "consume ReservationCreated event" in {
      // When
      viewStore withSession { implicit s: JdbcBackend.Session =>
        projection.consume(ReservationCreated("reservation-1", "client-1"))
      }

      import dao.profile.simple._

      // Then
      viewStore withSession { implicit s: Session =>
        val reservations = dao.byId("reservation-1")
        assert(reservations.head.status == Opened)
      }
    }
  }

  "ReservationProjection" should {
    "consume ReservationConfirmed event" in {
      // Given
      import dao.profile.simple._

      viewStore withSession { implicit s: Session =>
        dao.createIfNotExists(ReservationView("reservation-1", "client-1", Opened, new Date(now.getMillis)))
      }
        // When
      viewStore withSession { implicit s: JdbcBackend.Session =>
        projection.consume(ReservationConfirmed("reservation-1", "client-1", None))
      }

      // Then
      viewStore withSession { implicit s: Session =>
        val reservations = dao.byId("reservation-1")
        assert(reservations.head.status == Confirmed)
      }
    }
  }

  override def dropSchema(implicit s: JdbcBackend.Session): Unit = {
    dao.dropSchema
  }

  override def createSchema(implicit s: JdbcBackend.Session): Unit = {
    dao.createSchema
  }

  implicit def toEventMessage(event: ReservationCreated): DomainEventMessage = DomainEventMessage(AggregateSnapshotId(event.reservationId), event)
  implicit def toEventMessage(event: ReservationConfirmed): DomainEventMessage = DomainEventMessage(AggregateSnapshotId(event.reservationId), event)

}
