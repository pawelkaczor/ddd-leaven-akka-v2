package ecommerce.shipping

import com.typesafe.config.Config
import ecommerce.shipping
import ecommerce.shipping.view.{ShipmentProjection, ShipmentDao}
import pl.newicom.dddd.view.sql.{SqlViewUpdateConfig, SqlViewUpdateService, ViewMetadataDao}

import scala.slick.driver.JdbcProfile

class ShippingViewUpdateService(override val config: Config)(override implicit val profile: JdbcProfile)
  extends SqlViewUpdateService with ShippingReadBackendConfiguration {

  val shippingStream = shipping.streamName
  lazy val shipmentDao: ShipmentDao = new ShipmentDao()

  override def configuration: Seq[SqlViewUpdateConfig] = {
    List(
      SqlViewUpdateConfig("shipping-shipments", shippingStream, new ShipmentProjection(shipmentDao))
    )
  }

  override def onUpdateStart(): Unit = {
    viewStore withSession { implicit s =>
      new ViewMetadataDao().create
      shipmentDao.createSchema
    }
  }
}