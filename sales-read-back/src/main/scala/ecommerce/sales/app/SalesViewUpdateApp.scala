package ecommerce.sales.app

import akka.actor._
import akka.kernel.Bootable
import com.typesafe.config.ConfigFactory
import ecommerce.sales.SalesViewUpdateService

import scala.slick.driver.{JdbcProfile, PostgresDriver}

class SalesViewUpdateApp extends Bootable {
  private val config = ConfigFactory.load()
  val system = ActorSystem("sales-view-update", config)

  def startup() = {
    implicit val profile: JdbcProfile = PostgresDriver
    system.actorOf(Props(new SalesViewUpdateService(config)), "sales-view-update-service")
  }

  def shutdown() = {
    system.shutdown()
  }
}