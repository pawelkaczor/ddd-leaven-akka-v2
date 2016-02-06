import sbt._

object Deps {

  object Version {
    val akka        =    "2.4.1"
    val akkaHttp    =    "2.0.2"
    val akkaDDD     =    "1.1.0-SNAPSHOT"
    val Kamon       =    "0.6.0-a9d5c5c61f7e5e189bf67baee2b13e21ebbaaf73"
  }

  object Akka {
    val actor =            apply("actor")
    val httpTestKit =      apply("http-testkit-experimental", Version.akkaHttp) % "test"
    val multiNodeTestkit = apply("multi-node-testkit") % "test"

    private def apply(m: String, v: String = Version.akka) = "com.typesafe.akka" %% s"akka-$m" % v
  }

  object AkkaDDD {
    val core          = apply("core")
    val eventStore    = "pl.newicom.dddd" %% "eventstore-akka-persistence" % Version.akkaDDD
    val httpSupport   = Seq("pl.newicom.dddd" %% s"http-support" % Version.akkaDDD, Akka.httpTestKit)
    val messaging     = apply("messaging")
    val monitoring    = apply("monitoring")
    val scheduling    = apply("scheduling")
    val test          = apply("test") % "test"
    val viewUpdateSql = SqlDb() ++ Seq("pl.newicom.dddd" %% "view-update-sql" % Version.akkaDDD)
    val writeFront    = apply("write-front")

    private def apply(m: String) = "pl.newicom.dddd" %% s"akka-ddd-$m" % Version.akkaDDD
  }

  object Kamon {
    val akka          = apply("akka")
    val aspectjweaver = "org.aspectj" % "aspectjweaver" % "1.8.7"
    val akkaRemote    = apply("akka-remote_akka-2.4")
    val autoweave     = apply("autoweave")
    val core          = apply("core")
    val logReporter   = apply("log-reporter")
    val scala         = apply("scala")
    val statsD        = apply("statsd")
    val systemMetrics = apply("system-metrics")

    def apply(): Seq[ModuleID] = Seq(akka, akkaRemote, aspectjweaver, autoweave, scala, statsD)
    private def apply(m: String) = "io.kamon" %% s"kamon-$m" % Version.Kamon
  }

  object SqlDb {
    val `slick-for-pg` = "com.github.tminglei" %% "slick-pg" % "0.10.0" exclude("org.slf4j", "slf4j-simple")
    val testDriver = "com.h2database" % "h2" % "1.4.189" % "test"

    def prod = Seq(`slick-for-pg`)
    def apply() = Seq(`slick-for-pg`, testDriver)
  }

}
