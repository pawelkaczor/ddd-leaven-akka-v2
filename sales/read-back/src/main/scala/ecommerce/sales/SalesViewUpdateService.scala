package ecommerce.sales

import com.typesafe.config.Config
import ecommerce.sales.view.{ReservationDao, ReservationProjection}
import pl.newicom.dddd.view.sql.{SqlViewUpdateConfig, SqlViewUpdateService}
import slick.dbio.DBIO
import slick.driver.JdbcProfile

class SalesViewUpdateService(override val config: Config)(override implicit val profile: JdbcProfile)
  extends SqlViewUpdateService with SalesReadBackendConfiguration {

  lazy val resevationDao = new ReservationDao

  override def vuConfigs: Seq[SqlViewUpdateConfig] = {
    List(
      SqlViewUpdateConfig("sales-reservations", salesOffice, new ReservationProjection(resevationDao))
    )
  }

  override def onViewUpdateInit: DBIO[Unit] = {
      super.onViewUpdateInit >>
        resevationDao.ensureSchemaCreated
  }
}