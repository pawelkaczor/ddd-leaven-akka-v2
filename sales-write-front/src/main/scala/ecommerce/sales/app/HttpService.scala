package ecommerce.sales.app

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.Http
import akka.http.server.{Directives, Route}
import akka.stream.scaladsl.ImplicitFlowMaterializer
import akka.util.Timeout
import ecommerce.sales
import ecommerce.sales.{Command => SalesCommand}
import org.json4s.ext.{JodaTimeSerializers, UUIDSerializer}
import org.json4s.{DefaultFormats, Formats}
import pl.newicom.dddd.aggregate.Command
import pl.newicom.dddd.writefront.{CommandDirective, CommandHandler, JsonMarshalling}

import scala.concurrent.duration.FiniteDuration

object HttpService {
  def props(interface: String, port: Int, askTimeout: FiniteDuration): Props =
    Props(new HttpService(interface, port)(askTimeout))
}

class HttpService(interface: String, port: Int)(implicit askTimeout: Timeout) extends Actor with SalesFrontConfiguration
  with CommandDirective with Directives
  with ActorLogging with ImplicitFlowMaterializer with JsonMarshalling {

  import context.dispatcher
  implicit val formats: Formats = DefaultFormats ++ JodaTimeSerializers.all + UUIDSerializer + sales.typeHints

  lazy val salesOffice = office(sales.officeName)

  lazy val commandHandler = CommandHandler(context.system)

  Http()(context.system).bind(interface, port).startHandlingWith(route)
  log.info(s"Listening on $interface:$port")

  override def receive = Actor.emptyBehavior

  private def route = pathPrefix("ecommerce") {
    path("sales") {
      handleCommand[SalesCommand]
    }
  }

  private def handleCommand[A <: Command]: Route = commandManifest[A] { implicit cm =>
    post {
      entity(as[A]) { command =>
        complete {
          commandHandler.handle(salesOffice, command)
        }
      }
    }
  }

}