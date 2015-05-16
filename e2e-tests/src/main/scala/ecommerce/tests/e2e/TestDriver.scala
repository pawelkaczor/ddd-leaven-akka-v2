package ecommerce.tests.e2e

import org.iainhull.resttest.{Api, Dsl, RestMatchers}
import org.json4s.Formats
import org.json4s.native.Serialization.write
import org.scalatest.{Matchers, WordSpecLike}
import pl.newicom.dddd.aggregate.Command

trait TestDriver extends WordSpecLike with Dsl with Matchers with RestMatchers {
  this: Api =>

  def defBuilder: RequestBuilder

  def using(endpoint: EndpointConfig)(testBody: RequestBuilder => Unit): Unit = {
    testBody(defBuilder.withUrl(endpoint.toUrl))
  }

  implicit def methodToCommandRB(method: Method)(implicit b: RequestBuilder): CommandRequestBuilder =
    CommandRequestBuilder(b.withMethod(method))

  implicit class CommandRequestBuilder(builder: RequestBuilder) extends RichRequestBuilder(builder) {
    def command(c: Command)(implicit formats: Formats) =
      builder.withBody(write(c)).addHeaders(("Command-Type", c.getClass.getName))

    def /(subPath: String) = builder.withUrl(s"${builder.url.get}/$subPath")
  }

}