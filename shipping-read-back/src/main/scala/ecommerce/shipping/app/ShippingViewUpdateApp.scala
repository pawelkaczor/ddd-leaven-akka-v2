package ecommerce.shipping.app

import akka.actor._
import akka.kernel.Bootable
import com.typesafe.config.ConfigFactory
import ecommerce.shipping.ShippingViewUpdateService

import scala.slick.driver.{JdbcProfile, PostgresDriver}

class ShippingViewUpdateApp extends Bootable {
  private val config = ConfigFactory.load()
  val system = ActorSystem("shipping-view-update", config)

  def startup() = {
    implicit val profile: JdbcProfile = PostgresDriver
    system.actorOf(Props(new ShippingViewUpdateService(config)), "shipping-view-update-service")
  }

  def shutdown() = {
    system.terminate()
  }
}