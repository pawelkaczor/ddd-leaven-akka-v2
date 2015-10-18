package ecommerce.shipping.app

import akka.http.scaladsl.model.StatusCodes.NotFound
import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.ConfigFactory
import ecommerce.sales.view.ViewTestSupport
import ecommerce.shipping.{ShippingSerializationHintsProvider, ShippingStatus}
import ecommerce.shipping.view.{ShipmentDao, ShipmentView}
import org.json4s.Formats
import org.scalatest.{BeforeAndAfter, Matchers, WordSpecLike}
import pl.newicom.dddd.serialization.JsonSerHints._
import pl.newicom.dddd.utils.UUIDSupport.uuid7

class ShipmentViewEndpointSpec extends WordSpecLike with Matchers with ScalatestRouteTest with ViewTestSupport with BeforeAndAfter {

  override lazy val config = ConfigFactory.load
  implicit val formats: Formats = new ShippingSerializationHintsProvider().hints()

  lazy val dao = new ShipmentDao
  val shipmentId = uuid7

  before {
    viewStore.run {
      dao.createOrUpdate(ShipmentView(shipmentId, "order-1", ShippingStatus.Delivered))
    }.futureValue
  }

  after {
    viewStore.run {
      dao.remove(shipmentId)
    }.futureValue
  }

  "Shipment view endpoint" should {

    def response = responseAs[String]

    val route: Route = ShipmentViewEndpoint().route(viewStore)

    "respond to /shipment/all with all shipments" in {
      Get("/shipment/all") ~> route ~> check {
        response should include (shipmentId)
      }
    }

    "respond to /shipment/{shipmentId} with requested shipment" in {
      Get(s"/shipment/$shipmentId") ~> route ~> check {
        response should include (shipmentId)
      }
    }

    "respond to /shipment/{shipmentId} with NotFound if shipment unknown" in {
      Get(s"/shipment/invalid") ~> route ~> check {
        status shouldBe NotFound
      }
    }

  }

  def ensureSchemaDropped = dao.ensureSchemaDropped
  def ensureSchemaCreated = dao.ensureSchemaCreated

}