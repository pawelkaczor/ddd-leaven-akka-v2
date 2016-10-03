package ecommerce.sales.app

import akka.kernel.Bootable
import com.typesafe.config.Config
import ecommerce.sales.{HttpService, SalesReadFrontConfiguration}

class SalesReadFrontApp extends Bootable {

  override def systemName = "sales-read-front"

  def startup() = {
     new SalesReadFrontConfiguration {
       override def config: Config = SalesReadFrontApp.this.config
       import httpService._
       system.actorOf(HttpService.props(interface, port, askTimeout), "http-service")
     }
   }

}