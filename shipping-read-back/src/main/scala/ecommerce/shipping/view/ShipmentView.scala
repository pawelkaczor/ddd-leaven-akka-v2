package ecommerce.shipping.view

import ecommerce.shipping.ShippingStatus
import ShippingStatus.ShippingStatus
import pl.newicom.dddd.aggregate.EntityId

case class ShipmentView(id: EntityId, orderId: EntityId, status: ShippingStatus)
