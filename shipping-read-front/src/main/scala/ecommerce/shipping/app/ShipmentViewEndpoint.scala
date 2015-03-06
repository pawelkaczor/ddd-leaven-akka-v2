package ecommerce.shipping.app

import akka.http.server.PathMatchers.Segment
import akka.http.server.Route
import ecommerce.shipping
import ecommerce.shipping.ReadEndpoint
import ecommerce.shipping.view.ShipmentDao
import org.json4s.Formats

import scala.concurrent.ExecutionContext
import scala.slick.driver.JdbcProfile
import scala.slick.jdbc.JdbcBackend._

case class ShipmentViewEndpoint(implicit val ec: ExecutionContext, profile: JdbcProfile) extends ReadEndpoint {

  implicit val formats: Formats = defaultFormats + shipping.json.typeHints + shipping.json.formats

  lazy val dao = new ShipmentDao

  def route(viewStore: Database): Route = {
    path("shipment" / "all") {
      get {
        complete {
          viewStore withSession { implicit s =>
            dao.all
          }
        }
      }
    } ~
    path("shipment" / Segment) { id =>
      get {
        complete {
          viewStore withSession { implicit s =>
            dao.byId(id)
          }
        }
      }
    }

  }
}
