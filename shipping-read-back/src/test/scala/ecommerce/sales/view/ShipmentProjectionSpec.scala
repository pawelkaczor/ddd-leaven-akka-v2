package ecommerce.sales.view

import com.typesafe.config.{Config, ConfigFactory}
import ecommerce.shipping.view.{ShipmentProjection, ShipmentDao}
import ecommerce.shipping.{ShippingStatus, ShipmentCreated}
import ShippingStatus.Waiting
import ecommerce.shipping.ShipmentCreated
import org.scalatest._
import pl.newicom.dddd.messaging.event.{AggregateSnapshotId, DomainEventMessage}

import scala.slick.jdbc.JdbcBackend

class ShipmentProjectionSpec extends WordSpecLike with Matchers with ViewTestSupport {

  override def config: Config = ConfigFactory.load()

  val dao = new ShipmentDao
  val projection = new ShipmentProjection(dao)

  import dao.profile.simple._

  "ShipmentProjection" should {
    "consume ShipmentCreated event" in {
      // When
      viewStore withSession { implicit s: Session =>
        projection.consume(ShipmentCreated("shipment-1", "order-1"))
      }

      // Then
      viewStore withSession { implicit s: Session =>
        val shipments = dao.byId("shipment-1")
        assert(shipments.head.status == Waiting)
      }
    }
  }

  override def dropSchema(session: JdbcBackend.Session): Unit = {
    dao.dropSchema(session)
  }

  override def createSchema(session: JdbcBackend.Session): Unit = {
    dao.createSchema(session)
  }

  implicit def toEventMessage(event: ShipmentCreated): DomainEventMessage = DomainEventMessage(AggregateSnapshotId(event.shipmentId), event)

}
