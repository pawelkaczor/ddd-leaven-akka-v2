package ecommerce.shipping

import com.typesafe.config.Config
import ecommerce.shipping.view.{ShipmentDao, ShipmentProjection}
import pl.newicom.dddd.view.sql.{SqlViewUpdateConfig, SqlViewUpdateService}
import slick.dbio.DBIO
import slick.driver.JdbcProfile

class ShippingViewUpdateService(override val config: Config)(override implicit val profile: JdbcProfile)
  extends SqlViewUpdateService with ShippingReadBackendConfiguration {

  lazy val shipmentDao: ShipmentDao = new ShipmentDao()

  override def vuConfigs: Seq[SqlViewUpdateConfig] = {
    List(
      SqlViewUpdateConfig("shipping-shipments", shippingOffice, new ShipmentProjection(shipmentDao))
    )
  }

  override def onViewUpdateInit: DBIO[Unit] = {
      super.onViewUpdateInit >>
        shipmentDao.ensureSchemaCreated
  }
}