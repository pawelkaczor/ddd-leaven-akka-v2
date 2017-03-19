package ecommerce.shipping

import com.typesafe.config.Config

import scala.concurrent.duration._

trait ShippingReadFrontConfiguration {

  def config: Config

  object httpService {
    val interface =   appConfig.getString("http-service.interface")
    val port       =  appConfig.getInt("http-service.port")
    val askTimeout =  FiniteDuration(appConfig.getDuration("http-service.ask-timeout", MILLISECONDS), MILLISECONDS)
  }

  private val appConfig = config.getConfig("app")

}
