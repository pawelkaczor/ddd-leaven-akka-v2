package ecommerce.sales.app

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import pl.newicom.dddd.streams.ImplicitMaterializer
import akka.util.Timeout
import ecommerce.sales.{Command => ReservationCommand, ReservationOfficeId}
import org.json4s.Formats
import pl.newicom.dddd.aggregate.Command
import pl.newicom.dddd.http.JsonMarshalling
import pl.newicom.dddd.messaging.command.CommandMessage
import pl.newicom.dddd.office.OfficeId
import pl.newicom.dddd.serialization.JsonSerHints.fromConfig
import pl.newicom.dddd.utils.UUIDSupport._
import pl.newicom.dddd.writefront.{CommandDirectives, CommandHandler}

import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}

object HttpService {
  def props(interface: String, port: Int, askTimeout: FiniteDuration): Props =
    Props(new HttpService(interface, port)(askTimeout))
}

class HttpService(interface: String, port: Int)(implicit askTimeout: Timeout)
  extends Actor with SalesFrontConfiguration with CommandHandler
  with CommandDirectives with Directives
  with ActorLogging with ImplicitMaterializer with JsonMarshalling {

  import context.dispatcher
  implicit val formats: Formats = fromConfig(config)

  Http(context.system).bindAndHandle(route, interface, port)
  log.info(s"Listening on $interface:$port")

  override def receive = Actor.emptyBehavior

  private def route = /*logRequestResult("sales")*/ {
    pathPrefix("ecommerce") {
      path("sales") {
        handleCommand[ReservationCommand](ReservationOfficeId)
      }
    }
  }

  private def handleCommand[A <: Command](officeId: OfficeId): Route = commandTimestamp { timestamp =>
    commandManifest[A] { implicit cm =>
      post {
        entity(as[A]) { command =>
          complete {
            val cm = CommandMessage(command, uuid, timestamp.toDate)
            handle(officeId, cm).map[ToResponseMarshallable] {
              case Success(msg) => StatusCodes.OK -> msg
              case Failure(ex)  => StatusCodes.InternalServerError -> ex.getMessage
            }
          }
        }
      }
    }
  }

}