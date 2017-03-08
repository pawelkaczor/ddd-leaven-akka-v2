package ecommerce.shipping.app

import akka.actor._
import akka.kernel.Bootable
import ecommerce.shipping.ShippingViewUpdateService
import slick.jdbc.{JdbcProfile, PostgresProfile}

class ShippingViewUpdateApp extends Bootable {

  override def systemName = "shipping-view-update"

  def startup() = {
    implicit val profile: JdbcProfile = PostgresProfile
    system.actorOf(Props(new ShippingViewUpdateService(config)), "shipping-view-update-service")
  }

}