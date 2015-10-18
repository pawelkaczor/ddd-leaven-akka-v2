package ecommerce.sales.view

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, Suite}
import org.slf4j.LoggerFactory.getLogger
import pl.newicom.dddd.view.sql.SqlViewStoreConfiguration
import slick.dbio._

import scala.concurrent.ExecutionContext
import slick.driver.H2Driver

trait ViewTestSupport extends SqlViewStoreConfiguration with BeforeAndAfterAll with ScalaFutures {
  this: Suite =>

  val log = getLogger(getClass)

  implicit val profile = H2Driver

  implicit class ViewStoreAction[A](a: DBIO[A])(implicit ex: ExecutionContext) {
    private val future = viewStore.run(a)

    def run(): Unit = future.map(_ => ()).futureValue
    def result: A = future.futureValue
  }

  def ensureSchemaDropped: DBIO[Unit]
  def ensureSchemaCreated: DBIO[Unit]

  implicit override val patienceConfig = PatienceConfig(
    timeout = scaled(Span(5, Seconds)),
    interval = scaled(Span(200, Millis))
  )

  override def beforeAll() {
    viewStore.run {
      ensureSchemaDropped >> ensureSchemaCreated
    }.futureValue

  }

}