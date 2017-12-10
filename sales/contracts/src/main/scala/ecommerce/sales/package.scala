package ecommerce

import pl.newicom.dddd.aggregate.AggregateId
import pl.newicom.dddd.office.RemoteOfficeId

package object sales {

  type ReservationId = AggregateId
  val Department = "Sales"

  object ReservationOfficeId extends RemoteOfficeId[sales.Command](
    id           = "Reservation",
    department   = Department,
    commandClass = classOf[sales.Command]
  )

}
