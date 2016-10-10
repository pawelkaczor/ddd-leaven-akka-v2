package ecommerce

import pl.newicom.dddd.office.RemoteOfficeId

package object sales {

  implicit object ReservationOfficeId extends RemoteOfficeId[sales.Command](
    id           = "Reservation",
    department   = "Sales",
    messageClass = classOf[sales.Command]
  )

}
