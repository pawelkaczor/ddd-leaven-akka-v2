package ecommerce.sales.app

import akka.http.scaladsl.server.Route
import ecommerce.sales.ReadEndpoint
import ecommerce.sales.view.ReservationDao
import org.json4s.Formats

import scala.concurrent.ExecutionContext
import slick.driver.JdbcProfile
import slick.jdbc.JdbcBackend._

case class ReservationViewEndpoint(implicit ec: ExecutionContext, profile: JdbcProfile, formats: Formats) extends ReadEndpoint {

  lazy val dao = new ReservationDao

  def route(viewStore: Database): Route = {
    path("reservation" / "all") {
      get {
        complete {
          viewStore.run {
            dao.all
          }
        }
      }
    } ~
    path("reservation" / Segment) { id =>
      get {
        complete {
          viewStore.run {
            dao.byId(id)
          }
        }
      }
    }

  }
}
