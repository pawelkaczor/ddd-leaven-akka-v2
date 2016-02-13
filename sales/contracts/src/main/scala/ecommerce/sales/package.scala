package ecommerce

import pl.newicom.dddd.office.RemoteOfficeId

package object sales {

  implicit object SalesOfficeId extends RemoteOfficeId("Reservation", "Sales")

}
