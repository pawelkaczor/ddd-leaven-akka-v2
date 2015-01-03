package test.support

import com.typesafe.config.{Config, ConfigFactory}
import akka.actor.ActorSystem

object EventstoreTestConfig {
  val config = ConfigFactory.parseString(
    """akka.log-config-on-start = "off"
      |akka.loglevel = DEBUG
      |akka.loggers = ["akka.testkit.TestEventListener"]
      |akka.actor.debug.autoreceive = "on"
    """.stripMargin).withFallback(ConfigFactory.load("eventstore").withFallback(ConfigFactory.load("serialization")))

  def testSystem: ActorSystem = testSystem(EventstoreTestConfig.config)

  def testSystem(config: Config) = {
    config.resolve()
    ActorSystem("Tests", config)
  }

}
