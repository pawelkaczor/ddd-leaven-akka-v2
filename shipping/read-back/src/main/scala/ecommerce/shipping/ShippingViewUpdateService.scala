package ecommerce.shipping

import com.typesafe.config.Config
import ecommerce.shipping.view.{ShipmentDao, ShipmentProjection}
import eventstore.EsConnection
import pl.newicom.dddd.view.sql.{SqlViewUpdateConfig, SqlViewUpdateService}
import pl.newicom.eventstore.EventSourceProvider
import slick.dbio.DBIO
import slick.driver.JdbcProfile

class ShippingViewUpdateService(override val config: Config)(override implicit val profile: JdbcProfile)
  extends SqlViewUpdateService[EsConnection] with ShippingReadBackendConfiguration with EventSourceProvider {

  lazy val shipmentDao: ShipmentDao = new ShipmentDao()

  override def vuConfigs: Seq[SqlViewUpdateConfig] = {
    List(
      SqlViewUpdateConfig("shipping-shipments", ShippingOfficeId, new ShipmentProjection(shipmentDao))
    )
  }

  override def onViewUpdateInit: DBIO[Unit] = {
      super.onViewUpdateInit >>
        shipmentDao.ensureSchemaCreated
  }
}