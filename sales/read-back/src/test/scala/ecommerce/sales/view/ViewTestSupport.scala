package ecommerce.sales.view

import com.typesafe.config.Config
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, Suite}
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import pl.newicom.dddd.view.sql.SqlViewStore
import slick.dbio._

import scala.concurrent.ExecutionContext
import slick.jdbc.H2Profile

trait ViewTestSupport extends BeforeAndAfterAll with ScalaFutures {
  this: Suite =>

  def config: Config
  lazy val viewStore = new SqlViewStore(config)
  val log: Logger = getLogger(getClass)

  implicit val profile = H2Profile

  implicit class ViewStoreAction[A](a: DBIO[A])(implicit ex: ExecutionContext) {
    private val future = viewStore.run(a)

    def run(): Unit = future.map(_ => ()).futureValue
    def result: A = future.futureValue
  }

  def ensureSchemaDropped: DBIO[Unit]
  def ensureSchemaCreated: DBIO[Unit]

  implicit override val patienceConfig = PatienceConfig(
    timeout = scaled(Span(10, Seconds)),
    interval = scaled(Span(200, Millis))
  )

  override def beforeAll() {
    viewStore.run {
      ensureSchemaDropped >> ensureSchemaCreated
    }.futureValue

  }

}