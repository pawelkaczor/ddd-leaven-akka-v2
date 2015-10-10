package ecommerce

import pl.newicom.dddd.office.OfficeInfo

package object sales {

  implicit val salesOffice: OfficeInfo[SalesOffice] = OfficeInfo("Reservation", new SalesSerializationHints())
}
