package ecommerce.sales

import com.typesafe.config.Config
import ecommerce.sales.view.{ReservationDao, ReservationProjection}
import pl.newicom.dddd.view.sql.{SqlViewUpdateConfig, SqlViewUpdateService, ViewMetadataDao}
import slick.driver.JdbcProfile

import scala.concurrent.Future

class SalesViewUpdateService(override val config: Config)(override implicit val profile: JdbcProfile)
  extends SqlViewUpdateService with SalesReadBackendConfiguration {

  override def configuration: Seq[SqlViewUpdateConfig] = {
    List(
      SqlViewUpdateConfig("sales-reservations", salesOffice, new ReservationProjection(new ReservationDao))
    )
  }

  override def onUpdateStart: Future[Unit] = {
    viewStore.run(
      new ViewMetadataDao().ensureSchemaCreated >>
      new ReservationDao().ensureSchemaCreated
    ).mapToUnit
  }
}