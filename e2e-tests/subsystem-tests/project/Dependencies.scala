import sbt._
import sbt.Keys._

object Dependencies {

  object Version {
    val sales             = "0.1-SNAPSHOT"
    val json4s            = "3.2.11"
    val akka =              "2.4-SNAPSHOT"
  }

  object Ecommerce {
    val salesContracts = "pl.newicom" %% "sales-contracts" % Version.sales
  }

  object Akka {
    val actor =            apply("actor")

    private def apply(moduleName: String) = "com.typesafe.akka" %% s"akka-$moduleName" % Version.akka
  }

  object Json {
    val `4s`  = Seq(Json4s.native, Json4s.ext)
  }

  object Json4s {
    val native = apply("native")
    val ext = apply("ext")

    private def apply(moduleName: String) = "org.json4s" %% s"json4s-$moduleName" % Version.json4s
  }

  private val scalatest     = "org.scalatest"           %% "scalatest"          % "2.2.4"
  private val mockito       = "org.mockito"             % "mockito-core"        % "1.9.5"
  private val logback       = "ch.qos.logback"          %  "logback-classic"    % "1.1.2"
  private val nscalaTime    = "com.github.nscala-time"  %% "nscala-time"        % "1.4.0"
  private val config        = "com.typesafe"            %  "config"             % "1.2.1"
  private val commonsIo     = "commons-io"              %  "commons-io"         % "2.4"

  val common = deps(
    Akka.actor,
    nscalaTime, config, logback, Json4s.native, Json4s.ext,
    scalatest % "test", mockito % "test"
  )

  private def deps(modules: ModuleID*): Seq[Setting[_]] = Seq(libraryDependencies ++= modules)
}
