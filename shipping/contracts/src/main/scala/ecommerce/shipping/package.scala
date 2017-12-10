package ecommerce

import pl.newicom.dddd.aggregate.AggregateId
import pl.newicom.dddd.office.RemoteOfficeId

package object shipping {
  type ShipmentId = AggregateId
  val Department = "Shipping"

  implicit object ShippingOfficeId extends RemoteOfficeId[shipping.Command]("Shipment", Department, classOf[shipping.Command])
}
