import sbt._
import sbt.IO._
import sbt.Keys._
import scala.collection.immutable.Seq

object Settings {

  import BuildKeys._

  private lazy val general = Seq(
    version           <<= version in ThisBuild,
    scalaVersion      :=  "2.11.5",
    organization      :=  "pl.newicom",
    scalacOptions     ++= Seq("-unchecked", "-deprecation", "-feature", "-Xfuture", "-Xlint"),
    incOptions        :=  incOptions.value.withNameHashing(true),
    doc in Compile    <<= target.map(_ / "none"),
    vagrantFile       :=  (baseDirectory in ThisBuild).value / ".." / "Vagrantfile",
    resolvers         ++= Seq("Akka Snapshot Repository" at "http://repo.akka.io/snapshots/")
  )

  private lazy val shared = general ++ Testing.settings ++ Vagrant.settings

  lazy val root     = shared
  lazy val core     = shared ++ Dependencies.common
}
