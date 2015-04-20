import Dependencies.Ecommerce
import sbt._
import sbt.Keys._

object TheBuild extends Build {

  // -------------------------------------------------------------------------------------------------------------------
  // Root Project
  // -------------------------------------------------------------------------------------------------------------------

  lazy val root = Project("subsystem-tests", file("."))
    .aggregate(`sales-tests`)
    .settings(Settings.root)

  lazy val restTest =
    RootProject(uri("git://github.com/IainHull/resttest.git"))

  // -------------------------------------------------------------------------------------------------------------------
  // Modules
  // -------------------------------------------------------------------------------------------------------------------

  lazy val `sales-tests` = Project("sales-tests", file("sales-tests"))
    .settings(Settings.core)
    .settings(
      libraryDependencies ++= Seq(Ecommerce.salesContracts)
    )
    .dependsOn(restTest % "test->compile")
}
