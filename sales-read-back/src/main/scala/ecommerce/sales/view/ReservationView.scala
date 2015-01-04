package ecommerce.sales.view

import java.sql.Date

import pl.newicom.dddd.aggregate.EntityId

case class ReservationView(
  id: EntityId,
  clientId: EntityId,
  status: String,
  createDate: Date)
