package ecommerce.sales.view

import java.sql.Date

import ecommerce.sales.ReservationStatus
import ReservationStatus.ReservationStatus
import pl.newicom.dddd.aggregate.EntityId

case class ReservationView(
  id: EntityId,
  clientId: EntityId,
  status: ReservationStatus,
  createDate: Date)
