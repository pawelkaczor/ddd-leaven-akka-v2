package ecommerce.tests.e2e

import ecommerce.invoicing.{InvoicingSerializationHintsProvider, ReceivePayment, invoicingOffice}
import ecommerce.sales._
import ecommerce.shipping.{ShippingSerializationHintsProvider, shippingOffice}
import ecommerce.tests.e2e.SystemSpec._
import org.json4s.Formats
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Seconds, Span}
import pl.newicom.dddd.serialization.JsonSerHints._

object SystemSpec {

  val sales = EndpointConfig(path = "ecommerce/sales", officeInfo = salesOffice)
  val sales_write = sales.copy(port = 9100)
  val sales_read = sales.copy(port = 9110)

  val invoicing = EndpointConfig(path = "ecommerce/invoicing", officeInfo = invoicingOffice)
  val invoicing_write = invoicing.copy(port = 9200)

  val shipping = EndpointConfig(path = "ecommerce/shipping", officeInfo = shippingOffice)
  val shipping_read = shipping.copy(port = 9310)

  implicit val formats: Formats =
    new SalesSerializationHintsProvider().hints() ++
    new InvoicingSerializationHintsProvider().hints() ++
    new ShippingSerializationHintsProvider().hints()

}

class SystemSpec extends EcommerceSystemTestDriver with Eventually {

  implicit override val patienceConfig = PatienceConfig(
    timeout = scaled(Span(10, Seconds)),
    interval = scaled(Span(2, Seconds))
  )

  "Ecommerce system" should {

    val reservationId   = uuid7
    val invoiceId       = reservationId
    val customerId      = uuid7

    using(sales_write) { implicit b =>
      "create reservation" in eventually {
        POST command {
          CreateReservation(reservationId, customerId)
        } should succeed
      }
    }

    using(sales_read) { implicit b =>
      "respond to reservation/{reservationId} query" in eventually {
        GET / s"reservation/$reservationId" should succeed
      }
    }

    using(sales_write) { implicit b =>
      "reserve product" in {
        val product = Product(
          productId     = uuid7,
          name          = "DDDD For Dummies - 7th Edition",
          productType   = ProductType.Standard,
          price         = Some(Money(10.0))
        )
        POST command {
          ReserveProduct(reservationId, product, quantity = 1)
        } should succeed
      }

      "confirm reservation" in {
        POST command {
          ConfirmReservation(reservationId)
        } should succeed
      }
    }

    using(invoicing_write) { implicit b =>
      "pay" in eventually {
        POST command {
          ReceivePayment(invoiceId, reservationId, Money(10.0), paymentId = "230982342")
        } should succeed
      }
    }

    using(shipping_read) { implicit b =>
      "respond to /shipment/order/{orderId}" in eventually {
        GET / s"shipment/order/$reservationId" should succeed
      }
    }

  }
}
