package ecommerce.shipping

import ecommerce.shipping.view.{ShipmentDao, ShipmentProjection}
import pl.newicom.dddd.messaging.event.EventStoreProvider
import pl.newicom.dddd.view.sql.{SqlViewStore, SqlViewUpdateConfig, SqlViewUpdateService}
import pl.newicom.eventstore.EventSourceProvider
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

class ShippingViewUpdateService(viewStore: SqlViewStore)(override implicit val profile: JdbcProfile)
  extends SqlViewUpdateService(viewStore) with EventStoreProvider with EventSourceProvider {

  lazy val shipmentDao: ShipmentDao = new ShipmentDao()

  override def vuConfigs: Seq[SqlViewUpdateConfig] = {
    List(
      SqlViewUpdateConfig("shipping-shipments", ShippingOfficeId, new ShipmentProjection(shipmentDao))
    )
  }

  override def viewUpdateInitAction: DBIO[Unit] = {
      super.viewUpdateInitAction >>
        shipmentDao.ensureSchemaCreated
  }
}