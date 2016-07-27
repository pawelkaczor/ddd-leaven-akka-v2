package ecommerce.invoicing.app

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.Http
import akka.util.Timeout
import ecommerce.invoicing.{Command => InvoicingCommand, InvoicingOfficeId}
import org.json4s.Formats
import pl.newicom.dddd.serialization.JsonSerHints.fromConfig
import pl.newicom.dddd.writefront.CommandDispatcher

import scala.concurrent.duration.FiniteDuration

object HttpService {
  def props(interface: String, port: Int, askTimeout: FiniteDuration): Props =
    Props(new HttpService(interface, port)(askTimeout))
}

class HttpService(interface: String, port: Int)(implicit askTimeout: Timeout)
  extends Actor with InvoicingFrontConfiguration with CommandDispatcher with ActorLogging {

  implicit val formats: Formats = fromConfig(config)

  Http(context.system).bindAndHandle(route, interface, port)

  log.info(s"Listening on $interface:$port")

  override def receive = Actor.emptyBehavior

  override def offices = Set(InvoicingOfficeId)

  private def route = pathPrefix("ecommerce") {
    path("invoicing") {
      dispatch[InvoicingCommand]
    }
  }

}