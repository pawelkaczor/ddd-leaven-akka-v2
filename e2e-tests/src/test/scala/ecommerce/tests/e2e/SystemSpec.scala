package ecommerce.tests.e2e

import ecommerce.sales.{CreateReservation, salesOffice}
import ecommerce.tests.e2e.SystemSpec._
import org.iainhull.resttest.Api.Status.OK
import org.json4s.ext.{JodaTimeSerializers, UUIDSerializer}
import org.json4s.{DefaultFormats, Formats}
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Seconds, Span}

object SystemSpec {

  val sales = EndpointConfig(path = "ecommerce/sales")
  val sales_write = sales.copy(port = 9100)
  val sales_read = sales.copy(port = 9110)

  implicit val formats: Formats = salesOffice.serializationHints ++ DefaultFormats ++ JodaTimeSerializers.all + UUIDSerializer
}

class SystemSpec extends EcommerceSystemTestDriver with Eventually {

  implicit override val patienceConfig = PatienceConfig(
    timeout = scaled(Span(10, Seconds)),
    interval = scaled(Span(2, Seconds))
  )

  "Ecommerce system" should {

    val reservationId = uuid7
    val customerId = uuid7

    using(sales_write) { implicit b =>
      "create reservation" in {
        eventually {
          POST command CreateReservation(reservationId, customerId) should have(StatusCode(OK))
        }
      }
    }

    using(sales_read / s"reservation/$reservationId") { implicit b =>
      "respond to reservation/{reservationId} query" in {
        GET should have (StatusCode(OK))
      }
    }

  }
}
