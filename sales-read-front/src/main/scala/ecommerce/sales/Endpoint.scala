package ecommerce.sales

import akka.http.server.{Directives, Route}
import org.json4s.ext.{JodaTimeSerializers, UUIDSerializer}
import org.json4s.{DefaultFormats, Formats}

trait EndpointConcatenation {

  implicit def enhanceEndpointWithConcatenation[A](endpoint: Endpoint[A]): EndpointConcatenation[A] =
    new EndpointConcatenation(endpoint)

  class EndpointConcatenation[A](endpoint: Endpoint[A]) {
    def ~ (other: Endpoint[A]): Endpoint[A] = new Endpoint[A] {
      override def route(us: A): Route = {
        endpoint.route(us) ~ other.route(us)
      }
    }
  }

}

object EndpointConcatenation extends EndpointConcatenation

abstract class Endpoint[A] extends (A => Route) with Directives with JsonMarshalling with EndpointConcatenation {
  implicit val defaultFormats: Formats = DefaultFormats ++ JodaTimeSerializers.all + UUIDSerializer

  def apply(a: A) = route(a)

  def route(a: A): Route
}
