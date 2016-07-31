package ecommerce.invoicing.app

import akka.actor._
import akka.japi.Util.immutableSeq

import scala.collection.immutable.Seq
import scala.concurrent.duration.{FiniteDuration, MILLISECONDS}

trait InvoicingFrontConfiguration {
  this: Actor =>

  object httpService {
    val interface =   appConfig.getString("http-service.interface")
    val port       =  appConfig.getInt("http-service.port")
    val timeout =  FiniteDuration(appConfig.getDuration("http-service.ask-timeout", MILLISECONDS), MILLISECONDS)
  }

  lazy val contactPoints: Seq[String] = immutableSeq(appConfig.getStringList("backend-contact-points"))

  private val appConfig = config.getConfig("app")

  def config = context.system.settings.config

}