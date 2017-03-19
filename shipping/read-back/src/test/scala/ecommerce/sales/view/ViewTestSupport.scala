package ecommerce.sales.view

import com.typesafe.config.Config
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, Suite}
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import pl.newicom.dddd.view.sql.SqlViewStore
import slick.dbio._
import slick.jdbc.H2Profile

import scala.concurrent.ExecutionContext

trait ViewTestSupport extends BeforeAndAfterAll with ScalaFutures {
  this: Suite =>

  def config: Config
  lazy val viewStore = new SqlViewStore(config)
  val log: Logger = getLogger(getClass)

  implicit val profile = H2Profile

  implicit override val patienceConfig = PatienceConfig(
    timeout = scaled(Span(5, Seconds)),
    interval = scaled(Span(200, Millis))
  )

  implicit class ViewStoreAction[A](a: DBIO[A])(implicit ex: ExecutionContext) {
    private val future = viewStore.run(a)

    def run(): Unit = future.map(_ => ()).futureValue
    def result: A = future.futureValue
  }

  def ensureSchemaDropped: DBIO[Unit]
  def ensureSchemaCreated: DBIO[Unit]

  override def beforeAll() {
    val setup = viewStore.run {
      ensureSchemaDropped >> ensureSchemaCreated
    }
    assert(setup.isReadyWithin(Span(5, Seconds)))

  }

}