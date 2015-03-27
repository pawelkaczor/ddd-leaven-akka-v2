import sbt._

object Deps {

  object Version {
    val akka =              "2.4-SNAPSHOT"
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
    val httpSupport = "pl.newicom.dddd" %% s"http-support" % Version.akkaDDD withSources()
    val viewUpdateSql = "pl.newicom.dddd" %% "view-update-sql" % Version.akkaDDD withSources()
    val eventStore = "pl.newicom.dddd" %% "eventstore-akka-persistence" % Version.akkaDDD withSources()
    val scheduling = apply("scheduling")
    val test = apply("test") % "test"
    private def apply(moduleName: String) = "pl.newicom.dddd" %% s"akka-ddd-$moduleName" % Version.akkaDDD withSources()
  }

  object Eventstore {
    val client = "pl.newicom.dddd" %% "eventstore-client" % "2.0.2-SNAPSHOT" withSources()
    val akkaJournal = "pl.newicom.dddd" %% "akka-persistence-eventstore" % "2.0.2-SNAPSHOT" withSources()
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
    val `slick-for-pg` = "com.github.tminglei" %% "slick-pg" % "0.8.2" exclude("org.slf4j", "slf4j-simple")
    val testDriver = "com.h2database" % "h2" % "1.3.170" % "test"

    def prod = Seq(`slick-for-pg`)

    def apply() = Seq(`slick-for-pg`, testDriver)
  }
}
