package ecommerce.sales.app

import akka.actor._
import akka.kernel.Bootable
import com.typesafe.config.{Config, ConfigFactory}
import ecommerce.sales.{HttpService, SalesReadFrontConfiguration}

class SalesReadFrontApp extends Bootable {
   val config = ConfigFactory.load()
   val system = ActorSystem("sales-read-front", config)

   def startup() = {
     new SalesReadFrontConfiguration {
       override def config: Config = SalesReadFrontApp.this.config
       import httpService._
       system.actorOf(HttpService.props(interface, port, askTimeout), "http-service")
     }
   }

   def shutdown() = {
     system.shutdown()
   }
 }