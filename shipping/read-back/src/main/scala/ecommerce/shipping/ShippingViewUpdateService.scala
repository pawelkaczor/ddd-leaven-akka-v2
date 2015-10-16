package ecommerce.shipping

import com.typesafe.config.Config
import ecommerce.shipping.view.{ShipmentDao, ShipmentProjection}
import pl.newicom.dddd.view.sql.{SqlViewUpdateConfig, SqlViewUpdateService, ViewMetadataDao}
import slick.driver.JdbcProfile

import scala.concurrent.Future

class ShippingViewUpdateService(override val config: Config)(override implicit val profile: JdbcProfile)
  extends SqlViewUpdateService with ShippingReadBackendConfiguration {

  lazy val shipmentDao: ShipmentDao = new ShipmentDao()

  override def configuration: Seq[SqlViewUpdateConfig] = {
    List(
      SqlViewUpdateConfig("shipping-shipments", shippingOffice, new ShipmentProjection(shipmentDao))
    )
  }

  override def onUpdateStart: Future[Unit] = {
    viewStore.run {
      new ViewMetadataDao().ensureSchemaCreated >>
      shipmentDao.ensureSchemaCreated
    }.mapToUnit
  }
}