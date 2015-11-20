import sbt._

object Deps {

  object Version {
    val akka =                  "2.4.0"
    val akkaDDD =               "1.1.0-SNAPSHOT"
  }

  object Akka {
    val actor =            apply("actor")
    val multiNodeTestkit = apply("multi-node-testkit") % "test"

    private def apply(moduleName: String, version: String = Version.akka) = "com.typesafe.akka" %% s"akka-$moduleName" % version
  }

  object AkkaDDD {
    val messaging = apply("messaging")
    val core = apply("core")
    val writeFront = apply("write-front")
    val httpSupport = "pl.newicom.dddd" %% s"http-support" % Version.akkaDDD % "test->test;compile->compile"
    val viewUpdateSql = "pl.newicom.dddd" %% "view-update-sql" % Version.akkaDDD % "test->test;compile->compile"
    val eventStore = "pl.newicom.dddd" %% "eventstore-akka-persistence" % Version.akkaDDD
    val scheduling = apply("scheduling")
    val test = apply("test") % "test"
    private def apply(moduleName: String) = "pl.newicom.dddd" %% s"akka-ddd-$moduleName" % Version.akkaDDD
  }

}
