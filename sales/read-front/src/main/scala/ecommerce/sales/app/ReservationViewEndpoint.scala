package ecommerce.sales.app

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import ecommerce.sales.ReadEndpoint
import ecommerce.sales.view.ReservationDao
import org.json4s.Formats
import pl.newicom.dddd.view.sql.SqlViewStore
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

case class ReservationViewEndpoint(implicit ec: ExecutionContext, profile: JdbcProfile, formats: Formats) extends ReadEndpoint {

  lazy val dao = new ReservationDao

  def route(viewStore: SqlViewStore): Route = {
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
        onSuccess(viewStore.run(dao.byId(id))) {
          case Some(res) => complete(res)
          case None => complete(StatusCodes.NotFound -> "unknown reservation")
        }
      }
    }

  }

}
