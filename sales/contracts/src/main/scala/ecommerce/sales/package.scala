package ecommerce

import pl.newicom.dddd.office.RemoteOfficeId

package object sales {

  implicit object ReservationOfficeId extends RemoteOfficeId("Reservation", "Sales")

}
