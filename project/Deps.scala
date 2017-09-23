import sbt._

object Deps {

  object Version {
    val akka        =    "2.5.4"
    val akkaDDD     =    "1.7.4-SNAPSHOT"
    val Kamon       =    "0.6.6"
    val KamonAutoWeave = "0.6.5"
  }

  object Akka {
    val actor            = apply("actor")
    val multiNodeTestkit = apply("multi-node-testkit") % "test"
    val httpHealth       = "io.github.lhotari" %% "akka-http-health" % "1.0.8"

    private def apply(m: String, v: String = Version.akka) = "com.typesafe.akka" %% s"akka-$m" % v
  }

  object AkkaDDD {
    val core          = apply("core")
    val eventStore    = "pl.newicom.dddd" %% "eventstore-akka-persistence" % Version.akkaDDD
    val httpSupport   = Seq("pl.newicom.dddd" %% s"http-support" % Version.akkaDDD)
    val messaging     = apply("messaging")
    val monitoring    = apply("monitoring")
    val scheduling    = apply("scheduling")
    val test          = apply("test") % "test"
    val viewUpdateSql = SqlDb() ++ Seq("pl.newicom.dddd" %% "view-update-sql" % Version.akkaDDD)
    val writeFront    = apply("write-front")

    private def apply(m: String) = "pl.newicom.dddd" %% s"akka-ddd-$m" % Version.akkaDDD
  }

  object Kamon {
    val akka          = apply("akka-2.4")
    val aspectjweaver = "org.aspectj" % "aspectjweaver" % "1.8.7"
    val akkaRemote    = apply("akka-remote-2.4")
    val autoweave     = "io.kamon" %% "kamon-autoweave" % Version.KamonAutoWeave
    val core          = apply("core")
    val logReporter   = apply("log-reporter")
    val scala         = apply("scala")
    val statsD        = apply("statsd")
    val systemMetrics = apply("system-metrics")

    def apply(): Seq[ModuleID] = Seq(akka, akkaRemote, aspectjweaver, autoweave, scala, statsD)
    private def apply(m: String) = "io.kamon" %% s"kamon-$m" % Version.Kamon
  }

  object SqlDb {
    val `slick-for-pg` = "com.github.tminglei" %% "slick-pg" % "0.15.3" exclude("org.slf4j", "slf4j-simple")
    val testDriver = "com.h2database" % "h2" % "1.4.189" % "test"

    def prod = Seq(`slick-for-pg`)
    def apply() = Seq(`slick-for-pg`, testDriver)
  }

  def lp(name: String): LocalProject = LocalProject(name)
}
