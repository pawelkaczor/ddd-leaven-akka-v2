package ecommerce.sales.app

import akka.actor._
import akka.kernel.Bootable
import ecommerce.sales.SalesViewUpdateService
import slick.jdbc.{JdbcProfile, PostgresProfile}

class SalesViewUpdateApp extends Bootable {

  override def systemName = "sales-view-update"

  def startup() = {
    implicit val profile: JdbcProfile = PostgresProfile
    system.actorOf(Props(new SalesViewUpdateService(config)), "sales-view-update-service")
  }

}