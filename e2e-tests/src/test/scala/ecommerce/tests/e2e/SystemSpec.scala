package ecommerce.tests.e2e

import ecommerce.invoicing.ReceivePayment
import ecommerce.sales._
import ecommerce.shipping.ShippingSerializationHintsProvider
import ecommerce.tests.e2e.SystemSpec._
import org.json4s.Formats
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Seconds, Span}
import pl.newicom.dddd.serialization.JsonSerHints._
import pl.newicom.dddd.utils.UUIDSupport.uuid7

object SystemSpec {

  val sales     = EndpointConfig(path = "ecommerce/sales")
  val invoicing = EndpointConfig(path = "ecommerce/invoicing")
  val shipping  = EndpointConfig(path = "ecommerce/shipping")

  val sales_write: EndpointConfig     = sales.copy(port = 9100)
  val invoicing_write: EndpointConfig = invoicing.copy(port = 9200)

  val sales_read: EndpointConfig    = sales.copy(port = 9110)
  val shipping_read: EndpointConfig = shipping.copy(port = 9310)

  implicit val formats: Formats =
    new SalesSerializationHintsProvider().hints() ++
      new ShippingSerializationHintsProvider().hints()

}

class SystemSpec extends TestDriver with Eventually {

  implicit override val patienceConfig = PatienceConfig(
    timeout = scaled(Span(10, Seconds)),
    interval = scaled(Span(2, Seconds))
  )

  "Ecommerce system" should {
    val reservationId = uuid7
    val invoiceId     = reservationId
    val customerId    = uuid7

    using(sales_write) { implicit b =>
      "create reservation" in eventually {
        POST command {
          CreateReservation(reservationId, customerId)
        }
      }
    }

    using(sales_read) { implicit b =>
      "respond to reservation/{reservationId} query" in eventually {
        GET / s"reservation/$reservationId"
      }
    }

    using(sales_write) { implicit b =>
      "reserve product" in eventually {
        val product = Product(
          productId = uuid7,
          name = "DDDD For Dummies - 7th Edition",
          productType = ProductType.Standard,
          price = Some(Money(10.0))
        )

        POST command {
          ReserveProduct(reservationId, product, quantity = 1)
        }
      }

      "confirm reservation" in eventually {
        POST command {
          ConfirmReservation(reservationId)
        }
      }
    }

    using(invoicing_write) { implicit b =>
      "pay" in eventually {
        POST command {
          ReceivePayment(invoiceId, reservationId, Money(10.0), paymentId = "230982342")
        }
      }
    }

    using(shipping_read) { implicit b =>
      "respond to /shipment/order/{orderId}" in eventually {
        GET / s"shipment/order/$reservationId"
      }
    }
  }
}
