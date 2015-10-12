package ecommerce

import pl.newicom.dddd.office.OfficeInfo

package object shipping {
  implicit val shippingOffice: OfficeInfo[ShippingOffice] = OfficeInfo("Shipment")
}
