import Deps._
import sbt.Keys._

lazy val commons = (project in file("."))
  .settings(
    libraryDependencies ++= Seq(
      Akka.actor
    )
  )