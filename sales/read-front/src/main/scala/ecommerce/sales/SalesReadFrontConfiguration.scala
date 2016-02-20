package ecommerce.sales

import com.typesafe.config.Config
import pl.newicom.dddd.view.sql.SqlViewStoreConfiguration

import scala.concurrent.duration._

trait SalesReadFrontConfiguration extends SqlViewStoreConfiguration {

  def config: Config

  object httpService {
    val interface =   appConfig.getString("http-service.interface")
    val port       =  appConfig.getInt("http-service.port")
    val askTimeout =  FiniteDuration(appConfig.getDuration("http-service.ask-timeout", MILLISECONDS), MILLISECONDS)
  }

  private val appConfig = config.getConfig("app")

}
