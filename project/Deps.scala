import sbt._

object Deps {

  object Version {
    val akka =                  "2.4.0"
    val akkaHttp =              "1.0"
    val akkaDDD =               "1.0.8-SNAPSHOT"
  }

  object Akka {
    val actor =            apply("actor")
    val httpTestKit =      apply("http-testkit-experimental", Version.akkaHttp) % "test"
    val persistence =      apply("persistence")
    val slf4j =            apply("slf4j")
    val testkit =          apply("testkit") % "test"
    val multiNodeTestkit = apply("multi-node-testkit") % "test"

    private def apply(moduleName: String, version: String = Version.akka) = "com.typesafe.akka" %% s"akka-$moduleName" % version
  }

  object AkkaDDD {
    val messaging = apply("messaging")
    val core = apply("core")
    val writeFront = apply("write-front")
    val httpSupport = Seq(
      "pl.newicom.dddd" %% s"http-support" % Version.akkaDDD,
      Akka.httpTestKit
    )
    val viewUpdateSql = "pl.newicom.dddd" %% "view-update-sql" % Version.akkaDDD
    val eventStore = "pl.newicom.dddd" %% "eventstore-akka-persistence" % Version.akkaDDD
    val scheduling = apply("scheduling")
    val test = apply("test") % "test"
    private def apply(moduleName: String) = "pl.newicom.dddd" %% s"akka-ddd-$moduleName" % Version.akkaDDD
  }

  object Json {
    val `4s`  = Seq(Json4s.native, Json4s.ext)
  }

  object Json4s {
    val native = apply("native")
    val ext = apply("ext")

    private def apply(moduleName: String) = "org.json4s" %% s"json4s-$moduleName" % "3.3.0"
  }

  object SqlDb {
    val `slick-for-pg` = "com.github.tminglei" %% "slick-pg" % "0.10.0" exclude("org.slf4j", "slf4j-simple")
    val testDriver = "com.h2database" % "h2" % "1.4.189" % "test"

    def prod = Seq(`slick-for-pg`)

    def apply() = Seq(`slick-for-pg`, testDriver)
  }
}
