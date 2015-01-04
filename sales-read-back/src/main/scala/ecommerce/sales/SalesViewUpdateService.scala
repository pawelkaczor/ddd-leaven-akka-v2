package ecommerce.sales

import com.typesafe.config.Config
import ecommerce.sales
import ecommerce.sales.view.{ReservationDao, ReservationProjection}
import pl.newicom.dddd.view.sql.{SqlViewUpdateConfig, SqlViewUpdateService}

import scala.slick.driver.JdbcProfile

class SalesViewUpdateService(override val config: Config)(override implicit val profile: JdbcProfile)
  extends SqlViewUpdateService with SalesReadBackendConfiguration {

  val salesStream = sales.streamName

  override def configuration: Seq[SqlViewUpdateConfig] = {
    List(
      SqlViewUpdateConfig("sales-reservations", salesStream, new ReservationProjection(new ReservationDao))
    )
  }

}