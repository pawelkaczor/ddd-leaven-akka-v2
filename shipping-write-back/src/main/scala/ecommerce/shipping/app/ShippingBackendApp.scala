package ecommerce.shipping.app

import java.net.InetAddress

import _root_.akka.cluster.Cluster
import akka.actor.{ActorSystem, AddressFromURIString, Props}
import akka.kernel.Bootable
import com.typesafe.config.{Config, ConfigFactory}
import ecommerce.shipping.Shipment
import org.slf4j.LoggerFactory._
import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.aggregate.AggregateRootActorFactory
import pl.newicom.dddd.cluster.DefaultShardResolution
import pl.newicom.dddd.eventhandling.EventPublisher
import pl.newicom.dddd.messaging.event.DomainEventMessage
import pl.newicom.dddd.office.Office._

import scala.io.Source
import scala.util.Try
import pl.newicom.dddd.cluster.ShardingSupport._

class ShippingBackendApp extends Bootable {

  private lazy val log = getLogger(this.getClass.getName)

  private val config: Config = ConfigFactory.load()
  implicit private val system = ActorSystem("shipping", config)

  trait LocalPublisher extends EventPublisher {
    override def publish(em: DomainEventMessage): Unit = {
      log.debug("Published: " + em)
    }
  }

  implicit object ShipmentARFactory extends AggregateRootActorFactory[Shipment] {
    override def props(pc: PassivationConfig) = Props(new Shipment(pc) with LocalPublisher)
  }

  implicit object ShipmentShardResolution extends DefaultShardResolution[Shipment]

  override def startup() = {
    joinCluster()
    openOffices()
  }

  def openOffices(): Unit = {
    office[Shipment]
  }

  /**
   * Join the cluster with the specified seed nodes and block until termination
   */
  def joinCluster(): Unit = {
    val seedList = seeds(config)
    log.info(s"Joining cluster with seed nodes: $seedList")
    Cluster(system).joinSeedNodes(seedList.toSeq)
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

  override def shutdown() = {
    system.terminate()
  }

}