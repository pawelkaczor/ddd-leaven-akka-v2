package ecommerce

import org.json4s.FullTypeHints
import org.json4s.ext.EnumSerializer
import pl.newicom.dddd.messaging.event.AggregateSnapshotId
import pl.newicom.dddd.office.OfficeInfo
import pl.newicom.dddd.serialization.JsonSerializationHints

package object sales {

  implicit val salesOffice: OfficeInfo[SalesOffice] = OfficeInfo("Sales", new JsonSerializationHints {
    val typeHints = ReservationCommands + ReservationEvents + SalesValueObjects
    val serializers = List(new EnumSerializer(ProductType))

    object ReservationCommands extends FullTypeHints(
      List(
        classOf[CreateReservation],
        classOf[ReserveProduct],
        classOf[ConfirmReservation],
        classOf[CloseReservation]
      ))

    object ReservationEvents extends FullTypeHints(
      List(
        classOf[ReservationCreated],
        classOf[ProductReserved],
        classOf[ReservationConfirmed],
        classOf[ReservationClosed]
      ))

    object SalesValueObjects extends FullTypeHints(
      List(
        classOf[ReservationItem],
        classOf[Product],
        classOf[AggregateSnapshotId],
        classOf[Money]
      )
    )
  })
}
