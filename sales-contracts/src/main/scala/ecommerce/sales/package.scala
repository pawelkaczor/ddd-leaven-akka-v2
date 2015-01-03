package ecommerce

import org.json4s.{ShortTypeHints, TypeHints}

package object sales {
  val streamName = "Reservation"
  val officeName = "Reservation"

  implicit val typeHints: TypeHints = ReservationCommands + ReservationEvents + SalesValueObjects

  object ReservationCommands extends ShortTypeHints(
    List(
      classOf[CreateReservation],
      classOf[ReserveProduct],
      classOf[ConfirmReservation],
      classOf[CloseReservation]
    ))

  object ReservationEvents extends ShortTypeHints(
    List(
      classOf[ReservationCreated],
      classOf[ProductReserved],
      classOf[ReservationConfirmed],
      classOf[ReservationClosed]
    ))

  object SalesValueObjects extends ShortTypeHints(
    List(
      classOf[ReservationItem],
      classOf[Product],
      classOf[Money]
    )
  )
}
