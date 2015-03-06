package ecommerce.shipping

import ecommerce.shipping.ShippingReadBackendConfiguration

import scala.concurrent.duration._

trait ShippingReadFrontConfiguration extends ShippingReadBackendConfiguration {

  object httpService {
    val interface =   appConfig.getString("http-service.interface")
    val port       =  appConfig.getInt("http-service.port")
    val askTimeout =  FiniteDuration(appConfig.getDuration("http-service.ask-timeout", MILLISECONDS), MILLISECONDS)
  }

  private val appConfig = config.getConfig("app")

}
