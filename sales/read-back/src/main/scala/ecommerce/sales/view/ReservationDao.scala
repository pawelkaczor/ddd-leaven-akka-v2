package ecommerce.sales.view

import java.sql.Date
import ecommerce.sales.ReservationStatus
import ReservationStatus.ReservationStatus
import pl.newicom.dddd.aggregate.EntityId
import slick.jdbc.meta.MTable._
import scala.concurrent.ExecutionContext
import slick.driver.JdbcProfile

class ReservationDao(implicit val profile: JdbcProfile, ec: ExecutionContext)  {
  import profile.api._

  implicit val reservationStatusColumnType = MappedColumnType.base[ReservationStatus, String](
    { c => c.toString },
    { s => ReservationStatus.withName(s)}
  )

  val ReservationsTableName = "reservations"

  class Reservations(tag: Tag) extends Table[ReservationView](tag, ReservationsTableName) {
    def id = column[EntityId]("ID", O.PrimaryKey)
    def clientId = column[EntityId]("CLIENT_ID")
    def status = column[ReservationStatus]("STATUS")
    def createDate = column[Date]("CREATE_DATE")
    def * = (id, clientId, status, createDate) <> (ReservationView.tupled, ReservationView.unapply)
  }

  val reservations = TableQuery[Reservations]

  /**
   * Queries impl
   */
  private val by_id = reservations.findBy(_.id)
  private val by_client_id = reservations.findBy(_.clientId)


  /**
   * Public interface
   */

/*
  def createIfNotExists(view: ReservationView)(implicit s: Session): ReservationView = {
    by_id(view.id).run.headOption.orElse {
      reservations.insert(view)
      Some(view)
    }.get
  }
*/

  def createOrUpdate(view: ReservationView) = {
    reservations.insertOrUpdate(view)
  }

  def updateStatus(viewId: EntityId, status: ReservationStatus.Value) = {
    reservations.filter(_.id === viewId).map(_.status).update(status)
  }

  def all =  reservations.result

  def byId(id: EntityId) = by_id(id).result.headOption

  def byClientId(clientId: EntityId) = by_client_id(clientId).result

  def remove(id: EntityId) = by_id(id).delete

  def ensureSchemaDropped =
    getTables(ReservationsTableName).headOption.flatMap {
      case Some(table) => reservations.schema.drop.map(_ => ())
      case None => DBIO.successful(())
    }

  def ensureSchemaCreated =
    getTables(ReservationsTableName).headOption.flatMap {
      case Some(table) => DBIO.successful(())
      case None => reservations.schema.create.map(_ => ())
    }

}