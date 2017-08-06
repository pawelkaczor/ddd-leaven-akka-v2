import Deps._
import sbt.Keys._

lazy val monitoring = (project in file("."))
  .settings(
    libraryDependencies ++= Kamon()
  )
  .dependsOn(lp("commons"))