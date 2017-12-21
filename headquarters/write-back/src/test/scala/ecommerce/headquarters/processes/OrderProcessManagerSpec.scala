package ecommerce.headquarters.processes

import akka.actor.Props
import com.github.nscala_time.time.Imports._
import ecommerce.headquarters.processes.OrderProcessManagerSpec._
import ecommerce.invoicing.{CancelInvoice, CreateInvoice, InvoiceId, OrderBilled, OrderBillingFailed, PaymentExpired}
import ecommerce.sales._
import ecommerce.shipping.{CreateShipment, ShipmentId}
import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.aggregate.{AggregateId, EntityId}
import pl.newicom.dddd.process.SagaActorFactory
import pl.newicom.dddd.test.pm.PMSpec
import pl.newicom.dddd.test.support.TestConfig.testSystem
import pl.newicom.dddd.utils.UUIDSupport.uuid10

object OrderProcessManagerSpec {
  val shipmentId = new ShipmentId(uuid10)
  implicit object OrderProcessManagerActorFactory extends SagaActorFactory[OrderProcessManager] {
    def props(pc: PassivationConfig): Props = Props(new OrderProcessManager(pc, () => shipmentId))
  }

  def ids: (EntityId, ReservationId, InvoiceId) = {
    val orderId: EntityId = uuid10
    val reservationId: ReservationId = AggregateId(orderId)
    val invoiceId: InvoiceId = AggregateId(orderId)
    (orderId, reservationId, invoiceId)
  }
}

class OrderProcessManagerSpec extends PMSpec[OrderProcessManager](Some(testSystem)) {

  "Order Process Manager" should {

    "create invoice & schedule payment expiration on ReservationConfirmed event" in {
      val (orderId, reservationId, invoiceId) = ids

      when {
        ReservationConfirmed(reservationId, "customer-1", Some(Money(100d)))
      }
      .expectReceivedEvent
      .expect { e =>
        CreateInvoice(invoiceId, orderId, e.customerId, e.totalAmount.get, testEpoch)
      }
      .expectEventScheduled(testEpoch + 3.minutes) {
        PaymentExpired(invoiceId, orderId)
      }
    }

    "cancel invoice on PaymentExpired event" in {
      val (orderId, reservationId, invoiceId) = ids

      given {
        ReservationConfirmed(reservationId, "customer-1", Some(Money(100d)))
      }
      .when {
        PaymentExpired(invoiceId, orderId)
      }
      .expectReceivedEvent
      .expectCommand {
        CancelInvoice(invoiceId, orderId)
      }
    }

    "close reservation & create shipment on OrderBilled event" in {
      val (orderId, reservationId, invoiceId) = ids

      given {
        ReservationConfirmed(reservationId, "customer-1", Some(Money(100d)))
      }
      .when {
        OrderBilled(invoiceId, orderId, Money(100d), "payment-1")
      }
      .expectReceivedEvent
      .expectCommand {
        CloseReservation(reservationId)
      }
      .expectCommand {
        CreateShipment(shipmentId, orderId)
      }
    }

    "cancel reservation on OrderBillingFailed event" in {
      val (orderId, reservationId, invoiceId) = ids

      given {
        ReservationConfirmed(reservationId, "customer-1", Some(Money(100d)))
      }
      .when {
        OrderBillingFailed(invoiceId, orderId)
      }
      .expectReceivedEvent
      .expectCommand {
        CancelReservation(reservationId)
      }
    }

  }

}
