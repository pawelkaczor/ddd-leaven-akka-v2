package ecommerce.shipping

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.stream.scaladsl.ImplicitMaterializer
import akka.util.Timeout
import com.typesafe.config.Config
import ecommerce.shipping.app.ShipmentViewEndpoint

import scala.concurrent.duration.FiniteDuration
import scala.slick.driver.PostgresDriver

object HttpService {
  def props(interface: String, port: Int, askTimeout: FiniteDuration): Props =
    Props(new HttpService(interface, port)(askTimeout))
}

class HttpService(interface: String, port: Int)(implicit askTimeout: Timeout) extends Actor with ActorLogging
  with ShippingReadFrontConfiguration with ImplicitMaterializer with Directives {

  import context.dispatcher

  implicit val profile = PostgresDriver

  Http(context.system).bindAndHandle(route, interface, port)
  log.info(s"Listening on $interface:$port")

  override def receive = Actor.emptyBehavior
  override def config: Config = context.system.settings.config

  lazy val endpoints: ShipmentViewEndpoint = ShipmentViewEndpoint()

  private def route = (provide(viewStore) & pathPrefix("ecommerce" / "shipping"))(endpoints)

}
