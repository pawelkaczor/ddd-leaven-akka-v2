package ecommerce.sales.view

import org.scalatest.{BeforeAndAfterAll, Suite}
import org.slf4j.LoggerFactory.getLogger
import pl.newicom.dddd.view.sql.SqlViewStoreConfiguration

import scala.slick.driver.H2Driver
import scala.slick.jdbc.JdbcBackend

trait ViewTestSupport extends SqlViewStoreConfiguration with BeforeAndAfterAll {
  this: Suite =>

  val log = getLogger(getClass)

  implicit val profile = H2Driver

  def dropSchema(implicit s: JdbcBackend.Session)
  def createSchema(implicit s: JdbcBackend.Session)

  override def beforeAll() {
    log.debug("Starting views")

    import scala.slick.jdbc.JdbcBackend._

    viewStore withSession { implicit session: Session =>
      try {
        dropSchema(session)
      } catch {
        case ex: Exception => // ignore
      }
      createSchema(session)
    }

  }

}