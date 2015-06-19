package ecommerce.shipping.app

import java.net.InetAddress

import akka.actor._
import com.typesafe.config.Config
import ecommerce.shipping.Shipment
import org.slf4j.Logger
import pl.newicom.dddd.actor.{CreationSupport, PassivationConfig}
import pl.newicom.dddd.aggregate.AggregateRootActorFactory
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.eventhandling.EventPublisher
import pl.newicom.dddd.messaging.event.DomainEventMessage
import pl.newicom.dddd.process.Receptor
import pl.newicom.dddd.process.ReceptorSupport._
import pl.newicom.eventstore.EventstoreSubscriber

import scala.io.Source
import scala.util.Try

trait LocalPublisher extends EventPublisher {
  this: Actor with ActorLogging =>

  override def publish(em: DomainEventMessage): Unit = {
    context.system.eventStream.publish(em.event)
    log.debug(s"Published: $em")
  }
}

trait ShippingBackendConfiguration {

  def log: Logger
  def config: Config
  implicit def system: ActorSystem
  def creationSupport = implicitly[CreationSupport]

  //
  // Invoicing
  //
  implicit object ShipmentARFactory extends AggregateRootActorFactory[Shipment] {
    override def props(pc: PassivationConfig) = Props(new Shipment(pc) with LocalPublisher)
  }
  implicit object InvoiceShardResolution extends DefaultShardResolution[Shipment]

  //
  // Receptor factory
  //

  implicit val receptorFactory: ReceptorFactory = receptorConfig => {
    new Receptor with EventstoreSubscriber {
      def config = receptorConfig
    }
  }

  def seeds(config: Config) = {
    // Read cluster seed nodes from the file specified in the configuration
    Try(config.getString("app.cluster.seedsFile")).toOption match {
      case Some(seedsFile) =>
        // Seed file was specified, read it
        log.info(s"reading seed nodes from file: $seedsFile")
        Source.fromFile(seedsFile).getLines().map { address =>
          AddressFromURIString.parse(s"akka.tcp://shipping@$address")
        }.toList
      case None =>
        // No seed file specified, use this node as the first seed
        log.info("no seed file found, using default seeds")
        val port = config.getInt("app.port")
        val localAddress = Try(config.getString("app.host"))
          .toOption.getOrElse(InetAddress.getLocalHost.getHostAddress)
        List(AddressFromURIString.parse(s"akka.tcp://shipping@$localAddress:$port"))
    }
  }

}
