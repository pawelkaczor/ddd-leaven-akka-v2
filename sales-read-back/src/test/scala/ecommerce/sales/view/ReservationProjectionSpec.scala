package ecommerce.sales.view

import java.sql.Date

import com.typesafe.config.{Config, ConfigFactory}
import ecommerce.sales.{ReservationConfirmed, ReservationCreated, ReservationEvent, ReservationStatus}
import ReservationStatus.{Confirmed, Opened}
import ecommerce.sales.ReservationConfirmed
import org.joda.time.DateTime.now
import org.scalatest._
import pl.newicom.dddd.messaging.event.{AggregateSnapshotId, DomainEventMessage}

import scala.slick.jdbc.JdbcBackend

class ReservationProjectionSpec extends WordSpecLike with Matchers with ViewTestSupport {

  override def config: Config = ConfigFactory.load()

  val dao = new ReservationDao
  val projection = new ReservationProjection(dao)

  import dao.profile.simple._

  "ReservationProjection" should {
    "consume ReservationCreated event" in {
      // When
      viewStore withSession { implicit s: Session =>
        projection.consume(ReservationCreated("reservation-1", "client-1"))
      }

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
      viewStore withSession { implicit s: Session =>
        dao.createIfNotExists(ReservationView("reservation-1", "client-1", Opened, new Date(now.getMillis)))
      }
        // When
      viewStore withSession { implicit s: Session =>
        projection.consume(ReservationConfirmed("reservation-1", "client-1", None))
      }

      // Then
      viewStore withSession { implicit s: Session =>
        val reservations = dao.byId("reservation-1")
        assert(reservations.head.status == Confirmed)
      }
    }
  }

  override def dropSchema(session: JdbcBackend.Session): Unit = {
    dao.dropSchema(session)
  }

  override def createSchema(session: JdbcBackend.Session): Unit = {
    dao.createSchema(session)
  }

  implicit def toEventMessage(event: ReservationEvent): DomainEventMessage = DomainEventMessage(AggregateSnapshotId(event.reservationId), event)

}
