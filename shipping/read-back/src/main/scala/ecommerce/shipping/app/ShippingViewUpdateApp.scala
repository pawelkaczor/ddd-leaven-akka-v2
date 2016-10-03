package ecommerce.shipping.app

import akka.actor._
import akka.kernel.Bootable
import ecommerce.shipping.ShippingViewUpdateService
import slick.driver.{JdbcProfile, PostgresDriver}

class ShippingViewUpdateApp extends Bootable {

  override def systemName = "shipping-view-update"

  def startup() = {
    implicit val profile: JdbcProfile = PostgresDriver
    system.actorOf(Props(new ShippingViewUpdateService(config)), "shipping-view-update-service")
  }

}