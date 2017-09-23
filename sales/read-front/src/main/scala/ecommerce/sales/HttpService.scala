package ecommerce.sales

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import pl.newicom.dddd.streams.ImplicitMaterializer
import akka.util.Timeout
import com.typesafe.config.Config
import ecommerce.sales.app.ReservationViewEndpoint
import io.github.lhotari.akka.http.health.HealthEndpoint.createDefaultHealthRoute
import org.json4s.Formats
import pl.newicom.dddd.serialization.JsonSerHints._
import pl.newicom.dddd.view.sql.SqlViewStore

import scala.concurrent.duration.FiniteDuration
import slick.jdbc.{JdbcProfile, PostgresProfile}

object HttpService {
  def props(interface: String, port: Int, askTimeout: FiniteDuration): Props =
    Props(new HttpService(interface, port)(askTimeout))
}

class HttpService(interface: String, port: Int)(implicit askTimeout: Timeout) extends Actor with ActorLogging
  with SalesReadFrontConfiguration with ImplicitMaterializer with Directives {

  import context.dispatcher

  implicit val formats: Formats = fromConfig(config)
  implicit val profile: JdbcProfile = PostgresProfile

  Http(context.system).bindAndHandle(route, interface, port)

  log.info(s"Listening on $interface:$port")

  override def receive: Receive = Actor.emptyBehavior
  override def config: Config = context.system.settings.config

  lazy val endpoints: ReservationViewEndpoint = new ReservationViewEndpoint

  private def route = pathPrefix("ecommerce" / "sales") {
    createDefaultHealthRoute() ~
    provide(new SqlViewStore(config))(endpoints)
  }

}
