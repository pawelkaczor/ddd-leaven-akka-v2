package ecommerce.sales.app

import akka.actor._
import akka.kernel.Bootable
import ecommerce.sales.SalesViewUpdateService
import pl.newicom.dddd.view.sql.SqlViewStore
import slick.jdbc.{JdbcProfile, PostgresProfile}

class SalesViewUpdateApp extends Bootable {

  override def systemName = "sales-view-update"

  def startup(): Unit = {
    implicit val profile: JdbcProfile = PostgresProfile
    system.actorOf(Props(new SalesViewUpdateService(new SqlViewStore(config))), "sales-view-update-service")
  }

}