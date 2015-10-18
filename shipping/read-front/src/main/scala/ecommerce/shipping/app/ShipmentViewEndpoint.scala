package ecommerce.shipping.app

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import ecommerce.shipping.view.ShipmentDao
import ecommerce.shipping.ReadEndpoint
import org.json4s.Formats

import scala.concurrent.ExecutionContext
import slick.driver.JdbcProfile
import slick.jdbc.JdbcBackend._

case class ShipmentViewEndpoint(implicit val ec: ExecutionContext, profile: JdbcProfile, formats: Formats) extends ReadEndpoint {

  lazy val dao = new ShipmentDao

  def route(viewStore: Database): Route = {
    path("shipment" / "all") {
      get {
        complete {
          viewStore.run {
            dao.all
          }
        }
      }
    } ~
    path("shipment" / Segment) { id =>
      get {
        onSuccess(viewStore.run(dao.byId(id))) {
          case Some(res) => complete(res)
          case None => complete(StatusCodes.NotFound -> "unknown shipment")
        }
      }
    } ~
    path("shipment" / "order" / Segment) { id =>
      get {
        complete {
          viewStore.run {
            dao.byOrderId(id)
          }
        }
      }
    }

  }
}
