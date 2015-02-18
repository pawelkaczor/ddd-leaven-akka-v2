import sbt._

object Deps {

  object Version {
    val akka =              "2.3.9"
    val akkaHttp =          "1.0-M3"
    val akkaDDD =           "1.0.2-SNAPSHOT"
    val slick =             "2.1.0"
  }

  object Akka {
    val actor =            apply("actor")
    val httpCore =         "com.typesafe.akka" %% "akka-http-experimental" % Version.akkaHttp
    val httpTestKit =      "com.typesafe.akka" %% "akka-http-testkit-experimental" % Version.akkaHttp % "test"
    val persistence =      apply("persistence-experimental")
    val contrib =          apply("contrib")
    val kernel =           apply("kernel")
    val slf4j =            apply("slf4j")
    val testkit =          apply("testkit") % "test"
    val multiNodeTestkit = apply("multi-node-testkit") % "test"

    private def apply(moduleName: String) = "com.typesafe.akka" %% s"akka-$moduleName" % Version.akka withSources()
  }

  object AkkaDDD {
    val messaging = apply("messaging")
    val core = apply("core")
    val writeFront = apply("write-front")
    val viewUpdateSql = "pl.newicom.dddd" %% "view-update-sql" % Version.akkaDDD withSources()
    val eventStore = "pl.newicom.dddd" %% "eventstore-akka-persistence" % Version.akkaDDD withSources()
    val test = apply("test") % "test"
    private def apply(moduleName: String) = "pl.newicom.dddd" %% s"akka-ddd-$moduleName" % Version.akkaDDD withSources()
  }

  object Eventstore {
    val client = apply("eventstore-client", "2.0.0")
    val akkaJournal = apply("akka-persistence-eventstore", "2.0.2-SNAPSHOT")
    private def apply(moduleName: String, ver: String) = "com.geteventstore" %% moduleName % ver withSources()
  }

  object Json {
    val `4s`  = Seq(Json4s.native, Json4s.ext)
  }

  object Json4s {
    val native = apply("native")
    val ext = apply("ext")

    private def apply(moduleName: String) = "org.json4s" %% s"json4s-$moduleName" % "3.2.11" withSources()
  }

  object SqlDb {
    val slick = "com.typesafe.slick" %% "slick" % Version.slick
    val `slick-for-pg` = "com.github.tminglei" %% "slick-pg" % "0.7.0"
    val testDriver = "com.h2database" % "h2" % "1.3.170" % "test"

    def prod = Seq(`slick-for-pg`)

    def apply() = Seq(`slick-for-pg`, testDriver)
  }
}
