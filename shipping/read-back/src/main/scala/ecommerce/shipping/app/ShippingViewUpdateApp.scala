package ecommerce.shipping.app

import akka.actor._
import akka.kernel.Bootable
import ecommerce.shipping.ShippingViewUpdateService
import pl.newicom.dddd.view.sql.SqlViewStore
import slick.jdbc.{JdbcProfile, PostgresProfile}

class ShippingViewUpdateApp extends Bootable {

  override def systemName = "shipping-view-update"

  def startup() = {
    implicit val profile: JdbcProfile = PostgresProfile
    system.actorOf(Props(new ShippingViewUpdateService(new SqlViewStore(config))), "shipping-view-update-service")
  }

}