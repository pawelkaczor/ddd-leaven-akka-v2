import sbt._

object Deps {

  object Version {
    val akka =                  "2.4.0"
    val akkaHttp =              "1.0"
    val akkaDDD =               "1.1.0-SNAPSHOT"
  }

  object Akka {
    val actor =            apply("actor")
    val httpTestKit =      apply("http-testkit-experimental", Version.akkaHttp) % "test"
    val multiNodeTestkit = apply("multi-node-testkit") % "test"

    private def apply(moduleName: String, version: String = Version.akka) = "com.typesafe.akka" %% s"akka-$moduleName" % version
  }

  object AkkaDDD {
    val messaging = apply("messaging")
    val core = apply("core")
    val writeFront = apply("write-front")
    val httpSupport = Seq("pl.newicom.dddd" %% s"http-support" % Version.akkaDDD, Akka.httpTestKit)
    val viewUpdateSql = SqlDb() ++ Seq("pl.newicom.dddd" %% "view-update-sql" % Version.akkaDDD)
    val eventStore = "pl.newicom.dddd" %% "eventstore-akka-persistence" % Version.akkaDDD
    val scheduling = apply("scheduling")
    val test = apply("test") % "test"
    private def apply(moduleName: String) = "pl.newicom.dddd" %% s"akka-ddd-$moduleName" % Version.akkaDDD
  }


  object SqlDb {
    val `slick-for-pg` = "com.github.tminglei" %% "slick-pg" % "0.10.0" exclude("org.slf4j", "slf4j-simple")
    val testDriver = "com.h2database" % "h2" % "1.4.189" % "test"

    def prod = Seq(`slick-for-pg`)
    def apply() = Seq(`slick-for-pg`, testDriver)
  }
}
