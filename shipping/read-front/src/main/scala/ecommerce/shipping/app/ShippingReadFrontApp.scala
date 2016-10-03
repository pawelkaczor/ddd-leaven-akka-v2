package ecommerce.shipping.app

import akka.kernel.Bootable
import com.typesafe.config.Config
import ecommerce.shipping.{HttpService, ShippingReadFrontConfiguration}

class ShippingReadFrontApp extends Bootable {

  override def systemName = "shipping-read-front"

  def startup() = {
     new ShippingReadFrontConfiguration {
       override def config: Config = ShippingReadFrontApp.this.config
       import httpService._
       system.actorOf(HttpService.props(interface, port, askTimeout), "http-service")
     }
   }

}