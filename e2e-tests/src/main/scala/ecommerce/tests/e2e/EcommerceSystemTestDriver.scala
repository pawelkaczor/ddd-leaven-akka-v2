package ecommerce.tests.e2e

import org.iainhull.resttest.driver.Jersey
import pl.newicom.dddd.utils.UUIDSupport

trait EcommerceSystemTestDriver extends TestDriver with Jersey with UUIDSupport {

  val defBuilder = RequestBuilder.emptyBuilder.addHeaders(
    ("Content-Type", "application/json")
  )

}
