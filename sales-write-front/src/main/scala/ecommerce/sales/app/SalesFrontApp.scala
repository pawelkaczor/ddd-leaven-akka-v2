package ecommerce.sales.app

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, SupervisorStrategy, Terminated}
import akka.event.LoggingAdapter

object SalesFrontApp extends BaseApp {

  override def run(system: ActorSystem, log: LoggingAdapter): Unit = {
    system.actorOf(SalesFrontAppSupervisor.props, "sales-front-supervisor")
  }
}

object SalesFrontAppSupervisor {
  def props = Props(new SalesFrontAppSupervisor)
}

class SalesFrontAppSupervisor extends Actor with ActorLogging with SalesFrontConfiguration {

  override val supervisorStrategy = SupervisorStrategy.stoppingStrategy

  context.watch(createHttpService())

  override def receive: Receive = {
    case Terminated(ref) =>
      log.warning("Shutting down, because {} has terminated!", ref.path)
      context.system.shutdown()
  }

  protected def createHttpService(): ActorRef = {
    import httpService._
    context.actorOf(HttpService.props(interface, port, askTimeout), "http-service")
  }
}