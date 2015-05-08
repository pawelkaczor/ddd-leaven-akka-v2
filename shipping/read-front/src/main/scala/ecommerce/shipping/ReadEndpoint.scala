package ecommerce.shipping

import scala.slick.jdbc.JdbcBackend.Database
import pl.newicom.dddd.http.Endpoint

abstract class ReadEndpoint extends Endpoint[Database] {

}
