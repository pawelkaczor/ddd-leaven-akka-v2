package ecommerce.sales.view

import com.typesafe.config.{Config, ConfigFactory}
import ecommerce.shipping.{ShipmentCreated, ShipmentId, ShippingOfficeId}
import ecommerce.shipping.ShippingStatus.Waiting
import ecommerce.shipping.view.{ShipmentDao, ShipmentProjection}
import org.scalatest._
import pl.newicom.dddd.messaging.event.OfficeEventMessage
import pl.newicom.dddd.office.CaseRef

import scala.concurrent.ExecutionContext.Implicits.global

class ShipmentProjectionSpec extends WordSpecLike with Matchers with ViewTestSupport {

  override def config: Config = ConfigFactory.load()

  val dao = new ShipmentDao
  val projection = new ShipmentProjection(dao)

  "ShipmentProjection" should {
    "consume ShipmentCreated event" in {
      // When
      projection.consume(ShipmentCreated(new ShipmentId("shipment-1"), "order-1")).run()

      // Then
      assert(dao.byId("shipment-1").result.get.status == Waiting)
    }
  }

  override def ensureSchemaDropped = dao.ensureSchemaDropped
  override def ensureSchemaCreated = dao.ensureSchemaCreated

  implicit def toEventMessage(event: ShipmentCreated): OfficeEventMessage = OfficeEventMessage(CaseRef(event.shipmentId.value, ShippingOfficeId, None), event)

}
