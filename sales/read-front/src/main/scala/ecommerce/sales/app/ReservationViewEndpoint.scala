package ecommerce.sales.app

import akka.http.scaladsl.server.Route
import ecommerce.sales.view.ReservationDao
import ecommerce.sales.{ReadEndpoint, salesOffice}
import org.json4s.Formats

import scala.concurrent.ExecutionContext
import scala.slick.driver.JdbcProfile
import scala.slick.jdbc.JdbcBackend._

case class ReservationViewEndpoint(implicit val ec: ExecutionContext, profile: JdbcProfile) extends ReadEndpoint {

  implicit val formats: Formats = salesOffice.serializationHints ++ defaultFormats

  lazy val dao = new ReservationDao

  def route(viewStore: Database): Route = {
    path("reservation" / "all") {
      get {
        complete {
          viewStore withSession { implicit s =>
            dao.all
          }
        }
      }
    } ~
    path("reservation" / Segment) { id =>
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
