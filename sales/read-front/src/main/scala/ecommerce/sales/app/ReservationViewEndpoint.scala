package ecommerce.sales.app

import akka.http.scaladsl.server.Route
import com.typesafe.config.Config
import ecommerce.sales.ReadEndpoint
import ecommerce.sales.view.ReservationDao
import org.json4s.Formats

import scala.concurrent.ExecutionContext
import scala.slick.driver.JdbcProfile
import scala.slick.jdbc.JdbcBackend._

case class ReservationViewEndpoint(implicit ec: ExecutionContext, profile: JdbcProfile, formats: Formats) extends ReadEndpoint {

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
