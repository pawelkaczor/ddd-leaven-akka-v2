package ecommerce.sales

import akka.actor.{ActorRef, Props}
import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.aggregate.AggregateRootActorFactory
import pl.newicom.dddd.eventhandling.LocalPublisher
import pl.newicom.dddd.messaging.event.AggregateSnapshotId
import pl.newicom.dddd.test.support.OfficeSpec
import scala.concurrent.duration._

import scala.concurrent.duration.Duration
import ReservationSpec._

object ReservationSpec {
  implicit def factory(implicit it: Duration = 1.minute): AggregateRootActorFactory[Reservation] =
    new AggregateRootActorFactory[Reservation] {
      override def props(pc: PassivationConfig): Props = Props(new Reservation(pc) with LocalPublisher)
      override def inactivityTimeout: Duration = it
    }
}

class ReservationSpec extends OfficeSpec[Reservation] {

  def reservationOffice: ActorRef = officeUnderTest

  def reservationId = aggregateId

  val product = Product(AggregateSnapshotId("product1", 0), "productName", ProductType.Standard, Some(Money(10)))

  "Reservation office" should {
    "create reservation" in {
      when(
        CreateReservation(reservationId, "client1")
      )
      .expectEvent(
        ReservationCreated(reservationId, "client1")
      )
    }
  }

  "Reservation office" should {
    "reserve product" in {
      given(
        CreateReservation(reservationId, "client1")
      )
      .when(
        ReserveProduct(reservationId, product, quantity = 1)
      )
      .expectEvent {
        ProductReserved(reservationId, product, quantity = 1)
      }
    }
  }

  "Reservation office" should {
    "close reservation" in {
      given(
        CreateReservation(reservationId, "client1"),
        ReserveProduct(reservationId, product, quantity = 1)
      )
      .when(
        CloseReservation(reservationId)
      )
      .expectEvent {
        ReservationClosed(reservationId)
      }
    }
  }

}
