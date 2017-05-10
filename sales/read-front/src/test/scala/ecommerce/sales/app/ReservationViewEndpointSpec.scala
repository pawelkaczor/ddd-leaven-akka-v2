package ecommerce.sales.app

import java.sql.Date

import akka.http.scaladsl.model.StatusCodes.NotFound
import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.ConfigFactory
import ecommerce.sales.view.{ReservationDao, ReservationView, ViewTestSupport}
import ecommerce.sales.{ReservationStatus, SalesSerializationHintsProvider}
import org.joda.time.DateTime._
import org.json4s.Formats
import org.scalatest.{BeforeAndAfter, Matchers, WordSpecLike}
import pl.newicom.dddd.serialization.JsonSerHints._
import pl.newicom.dddd.utils.UUIDSupport.uuid7

class ReservationViewEndpointSpec extends WordSpecLike with Matchers with ScalatestRouteTest with ViewTestSupport with BeforeAndAfter {

  override lazy val config = ConfigFactory.load
  implicit val formats: Formats = new SalesSerializationHintsProvider().hints()

  lazy val dao = new ReservationDao
  val reservationId = uuid7

  before {
    viewStore.run {
      dao.createOrUpdate(ReservationView(reservationId, "client-1", ReservationStatus.Opened, new Date(now.getMillis)))
    }.futureValue
  }

  after {
    viewStore.run {
      dao.remove(reservationId)
    }.futureValue
  }

  "Reservation view endpoint" should {

    def response = responseAs[String]

    val route: Route = new ReservationViewEndpoint().route(viewStore)

    "respond to /reservation/all with all reservations" in {
      Get("/reservation/all") ~> route ~> check {
        response should include (reservationId)
      }
    }

    "respond to /reservation/{reservationId} with requested reservation" in {
      Get(s"/reservation/$reservationId") ~> route ~> check {
        response should include (reservationId)
      }
    }

    "respond to /reservation/{reservationId} with NotFound if reservation unknown" in {
      Get(s"/reservation/invalid") ~> route ~> check {
        status shouldBe NotFound
      }
    }

  }

  def ensureSchemaDropped = dao.ensureSchemaDropped
  def ensureSchemaCreated = dao.ensureSchemaCreated

}