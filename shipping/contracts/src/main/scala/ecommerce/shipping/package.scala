package ecommerce

import pl.newicom.dddd.office.RemoteOfficeId

package object shipping {
  implicit object ShippingOfficeId extends RemoteOfficeId("Shipment", "Shipping", classOf[shipping.Command])
}
