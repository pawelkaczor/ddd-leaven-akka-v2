package ecommerce.sales

import scala.concurrent.duration._

trait SalesReadFrontConfiguration extends SalesReadBackendConfiguration {

  object httpService {
    val interface =   appConfig.getString("http-service.interface")
    val port       =  appConfig.getInt("http-service.port")
    val askTimeout =  FiniteDuration(appConfig.getDuration("http-service.ask-timeout", MILLISECONDS), MILLISECONDS)
  }

  private val appConfig = config.getConfig("app")

}
