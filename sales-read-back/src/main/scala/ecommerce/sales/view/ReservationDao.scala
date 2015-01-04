package ecommerce.sales.view

import java.sql.Date

import pl.newicom.dddd.aggregate.EntityId

import scala.slick.driver.JdbcProfile
import scala.slick.jdbc.meta.MTable

class ReservationDao(implicit val profile: JdbcProfile)  {
  import profile.simple._

  object Reservations {
    val TableName = "reservations"
  }

  class Reservations(tag: Tag) extends Table[ReservationView](tag, Reservations.TableName) {
    def id = column[EntityId]("ID", O.PrimaryKey, O.NotNull)
    def clientId = column[EntityId]("CLIENT_ID", O.NotNull)
    def status = column[String]("STATUS", O.NotNull)
    def createDate = column[Date]("CREATE_DATE", O.NotNull)
    def * = (id, clientId, status, createDate) <> (ReservationView.tupled, ReservationView.unapply)
  }

  val reservations = TableQuery[Reservations]

  def createSchema(implicit s: Session) =
    if (MTable.getTables(Reservations.TableName).list.isEmpty) {
      reservations.ddl.create
    }

  def dropSchema(implicit s: Session) = reservations.ddl.drop

  /**
   * Queries impl
   */
  private val by_id = reservations.findBy(_.id)
  private val by_client_id = reservations.findBy(_.clientId)


  /**
   * Public interface
   */
  def createIfNotExists(view: ReservationView)(implicit s: Session): ReservationView = {
    by_id(view.id).run.headOption.orElse {
      reservations.insert(view)
      Some(view)
    }.get
  }

  def createOrUpdate(view: ReservationView)(implicit s: Session): ReservationView = {
    val query = by_id(view.id)
    if (query.run.headOption.isDefined)
      query.update(view)
    else
      reservations.insert(view)
    view
  }

  def update(view: ReservationView)(implicit s: Session) = reservations.update(view)

  def all(implicit s: Session) =  reservations.list

  def byId(id: EntityId)(implicit s: Session) = by_id(id).run.headOption

  def byClientId(clientId: EntityId)(implicit s: Session) = by_client_id(clientId).run.toList

  def remove(id: EntityId)(implicit s: Session) = by_id(id).delete
}