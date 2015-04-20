package pl.newicom

import org.iainhull.resttest.{RestMatchers, Dsl}
import org.iainhull.resttest.driver.JerseySystemTestDriver
import org.scalatest.{Matchers, WordSpecLike}

class SalesSpec extends WordSpecLike with Dsl with Matchers with RestMatchers with JerseySystemTestDriver {

  val baseUrl = "http://localhost/url"

  "/all" should {
    "be empty" in {
      GET / "all" should have(StatusCode(Status.OK))
    }
  }

}
