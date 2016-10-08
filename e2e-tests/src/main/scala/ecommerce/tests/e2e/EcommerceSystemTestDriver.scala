package ecommerce.tests.e2e

import org.iainhull.resttest.Api.Status._
import org.iainhull.resttest.driver.Jersey
import org.scalatest.BeforeAndAfterAll
import pl.newicom.dddd.utils.UUIDSupport

trait EcommerceSystemTestDriver extends TestDriver with Jersey with UUIDSupport with BeforeAndAfterAll {

  val defBuilder = RequestBuilder.emptyBuilder.addHeaders(
    ("Content-Type", "application/json")
  )


  def beOK = have(StatusCode(OK))
}
