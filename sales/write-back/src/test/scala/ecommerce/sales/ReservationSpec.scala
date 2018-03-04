package ecommerce.sales

import akka.actor.Props
import ecommerce.sales.ReservationSpec._
import pl.newicom.dddd.actor.{DefaultConfig, PassivationConfig}
import pl.newicom.dddd.aggregate.AggregateRootActorFactory
import pl.newicom.dddd.office.OfficeRef
import pl.newicom.dddd.test.ar.ARSpec

object ReservationSpec {
  implicit def factory: AggregateRootActorFactory[ReservationAggregateRoot] =
    new AggregateRootActorFactory[ReservationAggregateRoot] {
      def props(pc: PassivationConfig): Props = Props(new ReservationAggregateRoot(DefaultConfig(pc)))
    }
}

class ReservationSpec extends ARSpec[Event, ReservationAggregateRoot] {

  def reservationOffice: OfficeRef = officeUnderTest

  def reservationId: ReservationId = aggregateId

  val product = Product("product1", "productName", ProductType.Standard, Money(10))

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
      given {
        CreateReservation(reservationId, "client1")
      }
      .when {
        ReserveProduct(reservationId, product, quantity = 1)
      }
      .expectEvent {
        ProductReserved(reservationId, product, quantity = 1)
      }
    }
  }

  "Reservation office" should {
    "confirm reservation" in {
      given {
        CreateReservation(reservationId, "client1") &
        ReserveProduct(reservationId, product, quantity = 1)
      }
      .when(
        ConfirmReservation(reservationId)
      )
      .expectEvent {
        ReservationConfirmed(reservationId, "client1", product.price)
      }
    }
  }

  "Reservation office" should {
    "close reservation" in {
      given {
        CreateReservation(reservationId, "client1") &
        ReserveProduct(reservationId, product, quantity = 1)
      }
      .when {
        CloseReservation(reservationId)
      }
      .expectEvent {
        ReservationClosed(reservationId)
      }
    }
  }

}
