package ecommerce.sales

import com.typesafe.config.Config
import ecommerce.sales.view.{ReservationDao, ReservationProjection}
import pl.newicom.dddd.view.sql.{SqlViewUpdateConfig, SqlViewUpdateService}
import pl.newicom.eventstore.EventSourceProvider
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

class SalesViewUpdateService(override val config: Config)(override implicit val profile: JdbcProfile)
  extends SqlViewUpdateService with SalesReadBackendConfiguration with EventSourceProvider {

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