package ecommerce

import pl.newicom.dddd.aggregate.AggregateId
import pl.newicom.dddd.office.RemoteOfficeId

package object sales {

  type ReservationId = AggregateId

  implicit object ReservationOfficeId extends RemoteOfficeId[sales.Command](
    id           = "Reservation",
    department   = "Sales",
    messageClass = classOf[sales.Command]
  )

}
