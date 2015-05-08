package ecommerce.invoicing.app

import akka.actor._
import akka.kernel.Bootable
import com.typesafe.config.{Config, ConfigFactory}

class InvoicingFrontApp extends Bootable {

  private val config: Config = ConfigFactory.load()
  implicit private val system = ActorSystem("invoicing-front", config)

  override def startup(): Unit = {
    system.actorOf(InvoicingFrontAppSupervisor.props, "invoicing-front-supervisor")
  }

  override def shutdown(): Unit = {
    system.terminate()
  }
}

object InvoicingFrontAppSupervisor {
  def props = Props(new InvoicingFrontAppSupervisor)
}

class InvoicingFrontAppSupervisor extends Actor with ActorLogging with InvoicingFrontConfiguration {

  override val supervisorStrategy = SupervisorStrategy.stoppingStrategy

  context.watch(createHttpService())

  override def receive: Receive = {
    case Terminated(ref) =>
      log.warning("Shutting down, because {} has terminated!", ref.path)
      context.system.terminate()
  }

  protected def createHttpService(): ActorRef = {
    import httpService._
    context.actorOf(HttpService.props(interface, port, askTimeout), "http-service")
  }
}