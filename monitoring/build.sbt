import Deps._
import sbt.Keys._

lazy val monitoring = (project in file("."))
  .settings(
    resolvers += "Kamon Snapshots Repository" at "http://snapshots.kamon.io",
    libraryDependencies ++= Kamon()
  )
  .dependsOn("commons")