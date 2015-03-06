package ecommerce.shipping.view

import ecommerce.shipping.ShippingStatus
import ShippingStatus.ShippingStatus
import ecommerce.shipping.ShippingStatus
import pl.newicom.dddd.aggregate.EntityId

import scala.slick.driver.JdbcProfile
import scala.slick.jdbc.meta.MTable

class ShipmentDao(implicit val profile: JdbcProfile)  {
  import profile.simple._

  implicit val shipmentStatusColumnType = MappedColumnType.base[ShippingStatus, String](
    { c => c.toString },
    { s => ShippingStatus.withName(s)}
  )

  object Shipments {
    val TableName = "shippings"
  }

  class Shipments(tag: Tag) extends Table[ShipmentView](tag, Shipments.TableName) {
    def id = column[EntityId]("ID", O.PrimaryKey, O.NotNull)
    def orderId = column[EntityId]("ORDER_ID", O.NotNull)
    def status = column[ShippingStatus]("STATUS", O.NotNull)
    def * = (id, orderId, status) <> (ShipmentView.tupled, ShipmentView.unapply)
  }

  val shipments = TableQuery[Shipments]

  def createSchema(implicit s: Session) =
    if (MTable.getTables(Shipments.TableName).list.isEmpty) {
      shipments.ddl.create
    }

  def dropSchema(implicit s: Session) = shipments.ddl.drop

  /**
   * Queries impl
   */
  private val by_id = shipments.findBy(_.id)
  private val by_order_id = shipments.findBy(_.orderId)


  /**
   * Public interface
   */
  def createIfNotExists(view: ShipmentView)(implicit s: Session): ShipmentView = {
    by_id(view.id).run.headOption.orElse {
      shipments.insert(view)
      Some(view)
    }.get
  }

  def createOrUpdate(view: ShipmentView)(implicit s: Session): ShipmentView = {
    val query = by_id(view.id)
    if (query.run.headOption.isDefined)
      query.update(view)
    else
      shipments.insert(view)
    view
  }

  def update(view: ShipmentView)(implicit s: Session) = shipments.update(view)

  def all(implicit s: Session) =  shipments.list

  def byId(id: EntityId)(implicit s: Session) = by_id(id).run.headOption

  def byOrderId(orderId: EntityId)(implicit s: Session) = by_order_id(orderId).run.toList

}