import sbt._
import sbt.Keys._

name := "e2e-tests"

organization := "pl.newicom"

scalaVersion := "2.11.5"

scalacOptions := Seq("-encoding", "utf8", "-feature", "-language:postfixOps", "-language:implicitConversions", "-Xlint")

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
)

libraryDependencies ++= Seq(
  "org.yaml" % "snakeyaml" % "1.14",
  "pl.newicom" %% "resttest" % "0.2.0-SNAPSHOT",
  "pl.newicom" %% "sales-contracts" % "0.1-SNAPSHOT",
  "pl.newicom" %% "invoicing-contracts" % "0.1-SNAPSHOT",
  "pl.newicom" %% "shipping-contracts" % "0.1-SNAPSHOT"
)