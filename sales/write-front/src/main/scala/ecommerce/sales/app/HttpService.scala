package ecommerce.sales.app

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.scaladsl.ImplicitFlowMaterializer
import akka.util.Timeout
import ecommerce.sales.{Command => SalesCommand, salesOffice}
import org.json4s.ext.{JodaTimeSerializers, UUIDSerializer}
import org.json4s.{DefaultFormats, Formats}
import pl.newicom.dddd.aggregate.Command
import pl.newicom.dddd.messaging.command.CommandMessage
import pl.newicom.dddd.writefront.{CommandDirective, CommandHandler}
import pl.newicom.dddd.http.JsonMarshalling

import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}

object HttpService {
  def props(interface: String, port: Int, askTimeout: FiniteDuration): Props =
    Props(new HttpService(interface, port)(askTimeout))
}

class HttpService(interface: String, port: Int)(implicit askTimeout: Timeout)
  extends Actor with SalesFrontConfiguration with CommandHandler
  with CommandDirective with Directives
  with ActorLogging with ImplicitFlowMaterializer with JsonMarshalling {

  import context.dispatcher
  implicit val formats: Formats = salesOffice.serializationHints ++ DefaultFormats ++ JodaTimeSerializers.all + UUIDSerializer

  Http(context.system).bindAndHandle(route, interface, port)
  log.info(s"Listening on $interface:$port")

  override def receive = Actor.emptyBehavior

  private def route = pathPrefix("ecommerce") {
    path("sales") {
      handleCommand[SalesCommand](salesOffice.name)
    }
  }

  private def handleCommand[A <: Command](officeName: String): Route = commandManifest[A] { implicit cm =>
    post {
      entity(as[A]) { command =>
        complete {
          handle(officeName, CommandMessage(command)).map[ToResponseMarshallable] {
            case Success(msg) => StatusCodes.OK -> msg
            case Failure(ex)  => StatusCodes.InternalServerError -> ex.getMessage
          }
        }
      }
    }
  }

}