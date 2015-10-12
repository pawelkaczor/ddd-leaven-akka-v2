package ecommerce.shipping

import org.json4s.Formats

import scala.slick.jdbc.JdbcBackend.Database
import pl.newicom.dddd.http.Endpoint

abstract class ReadEndpoint(implicit formats: Formats) extends Endpoint[Database] {

}
