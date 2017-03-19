package ecommerce.sales

import ecommerce.sales.view.{ReservationDao, ReservationProjection}
import pl.newicom.dddd.view.sql.{SqlViewStore, SqlViewUpdateConfig, SqlViewUpdateService}
import pl.newicom.eventstore.{EventSourceProvider, EventStoreProvider}
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

class SalesViewUpdateService(viewStore: SqlViewStore)(override implicit val profile: JdbcProfile)
  extends SqlViewUpdateService(viewStore) with EventStoreProvider with EventSourceProvider {

  lazy val resevationDao = new ReservationDao

  override def vuConfigs: Seq[SqlViewUpdateConfig] = {
    List(
      SqlViewUpdateConfig("sales-reservations", ReservationOfficeId, new ReservationProjection(resevationDao))
    )
  }

  override def viewUpdateInitAction: DBIO[Unit] = {
      super.viewUpdateInitAction >>
        resevationDao.ensureSchemaCreated
  }
}