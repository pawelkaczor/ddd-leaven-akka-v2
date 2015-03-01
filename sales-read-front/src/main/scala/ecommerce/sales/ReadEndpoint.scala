package ecommerce.sales

import scala.slick.jdbc.JdbcBackend.Database
import pl.newicom.dddd.http.Endpoint

abstract class ReadEndpoint extends Endpoint[Database] {

}
