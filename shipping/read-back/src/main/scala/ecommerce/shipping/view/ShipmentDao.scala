package ecommerce.shipping.view

import ecommerce.shipping.ShippingStatus
import ecommerce.shipping.ShippingStatus.ShippingStatus
import pl.newicom.dddd.aggregate.EntityId
import slick.driver.JdbcProfile
import slick.jdbc.meta.MTable._

import scala.concurrent.ExecutionContext

class ShipmentDao(implicit val profile: JdbcProfile, ex: ExecutionContext) {
  import profile.api._

  implicit val shipmentStatusColumnType = MappedColumnType.base[ShippingStatus, String](
    { c => c.toString },
    { s => ShippingStatus.withName(s)}
  )

  val shipmentsTableName = "shippings"

  class Shipments(tag: Tag) extends Table[ShipmentView](tag, shipmentsTableName) {
    def id = column[EntityId]("ID", O.PrimaryKey)
    def orderId = column[EntityId]("ORDER_ID")
    def status = column[ShippingStatus]("STATUS")
    def * = (id, orderId, status) <> (ShipmentView.tupled, ShipmentView.unapply)
  }

  val shipments = TableQuery[Shipments]

  /**
   * Queries impl
   */
  private val by_id = shipments.findBy(_.id)
  private val by_order_id = shipments.findBy(_.orderId)


  /**
   * Public interface
   */
  def all =  shipments.result

  def byId(id: EntityId) = by_id(id).result.headOption

  def byOrderId(orderId: EntityId) = by_order_id(orderId).result

/*
  def createIfNotExists(view: ShipmentView)(implicit s: Session): ShipmentView = {
    byId(view.id).run.headOption.orElse {
      shipments.insert(view)
      Some(view)
    }.get
  }
*/

  def createOrUpdate(view: ShipmentView) = {
    shipments.insertOrUpdate(view)
  }

/*
  def update(view: ShipmentView) = {
    val query = by_id(view.id)
    if (query.run.headOption.isDefined)
      query.update(view)
    else
      throw new RuntimeException("view does not exist")
  }
*/


  def ensureSchemaDropped =
    getTables(shipmentsTableName).headOption.flatMap {
      case Some(table) => shipments.schema.drop.map(_ => ())
      case None => DBIO.successful(())
    }

  def ensureSchemaCreated =
    getTables(shipmentsTableName).headOption.flatMap {
      case Some(table) => DBIO.successful(())
      case None => shipments.schema.create.map(_ => ())
    }

}