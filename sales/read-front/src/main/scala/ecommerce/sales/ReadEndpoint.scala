package ecommerce.sales

import org.json4s.Formats
import pl.newicom.dddd.http.Endpoint

import slick.jdbc.JdbcBackend.Database

abstract class ReadEndpoint(implicit formats: Formats) extends Endpoint[Database] {

}
