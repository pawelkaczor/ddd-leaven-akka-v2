package ecommerce.sales.view

import com.typesafe.config.{Config, ConfigFactory}
import ecommerce.shipping.ShipmentCreated
import ecommerce.shipping.ShippingStatus.Waiting
import ecommerce.shipping.view.{ShipmentDao, ShipmentProjection}
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
      viewStore withSession { implicit s: JdbcBackend.Session =>
        projection.consume(ShipmentCreated("shipment-1", "order-1"))
      }

      // Then
      viewStore withSession { implicit s: Session =>
        val shipments = dao.byId("shipment-1")
        assert(shipments.head.status == Waiting)
      }
    }
  }

  override def dropSchema(implicit s: JdbcBackend.Session): Unit = {
    dao.dropSchema
  }

  override def createSchema(implicit s: JdbcBackend.Session): Unit = {
    dao.createSchema
  }

  implicit def toEventMessage(event: ShipmentCreated): DomainEventMessage = DomainEventMessage(AggregateSnapshotId(event.shipmentId), event)

}
