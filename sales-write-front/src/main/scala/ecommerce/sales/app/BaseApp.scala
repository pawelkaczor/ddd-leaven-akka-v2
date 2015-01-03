package ecommerce.sales.app

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}

import scala.collection.breakOut

object BaseApp {

  private val opt = """-D(\S+)=(\S+)""".r

  def applySystemProperties(args: Array[String]): Unit = {
    def argsToProps(args: Array[String]) =
      args.collect { case opt(key, value) => key -> value }(breakOut)
    for ((key, value) <- argsToProps(args))
      System.setProperty(key, value)
  }
}

abstract class BaseApp {

  import BaseApp._

  def main(args: Array[String]) {
    applySystemProperties(args)
    val system = ActorSystem("sales-front")
    val log = Logging.apply(system, getClass)

    run(system, log)
    log.info("App up and running")

    system.awaitTermination()
  }

  def run(system: ActorSystem, log: LoggingAdapter): Unit
}