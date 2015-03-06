package ecommerce.shipping.app

import akka.actor._
import akka.kernel.Bootable
import com.typesafe.config.{Config, ConfigFactory}
import ecommerce.shipping.{HttpService, ShippingReadFrontConfiguration}

class ShippingReadFrontApp extends Bootable {
   val config = ConfigFactory.load()
   val system = ActorSystem("shipping-read-front", config)

   def startup() = {
     new ShippingReadFrontConfiguration {
       override def config: Config = ShippingReadFrontApp.this.config
       import httpService._
       system.actorOf(HttpService.props(interface, port, askTimeout), "http-service")
     }
   }

   def shutdown() = {
     system.terminate()
   }
 }