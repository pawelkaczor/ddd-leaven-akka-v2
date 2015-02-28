package ecommerce

import org.json4s.ext.EnumSerializer
import org.json4s.{FullTypeHints, TypeHints}
import pl.newicom.dddd.messaging.event.AggregateSnapshotId

package object sales {
  val streamName = "Reservation"
  val officeName = "Reservation"

  implicit val typeHints: TypeHints = ReservationCommands + ReservationEvents + SalesValueObjects
  implicit val formats = new EnumSerializer(ProductType)

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
}
