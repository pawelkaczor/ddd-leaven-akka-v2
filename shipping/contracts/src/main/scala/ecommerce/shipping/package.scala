package ecommerce

import pl.newicom.dddd.office.RemoteOfficeId

package object shipping {
  implicit object ShippingOfficeId extends RemoteOfficeId[shipping.Command]("Shipment", "Shipping", classOf[shipping.Command])
}
