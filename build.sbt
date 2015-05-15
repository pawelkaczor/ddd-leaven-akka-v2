import sbt.Keys._

name := "ddd-leaven-akka-v2"

organization in ThisBuild := "pl.newicom"

scalaVersion in ThisBuild := "2.11.6"

scalacOptions in ThisBuild := Seq("-encoding", "utf8", "-feature", "-language:postfixOps", "-language:implicitConversions"/*, "-Xlog-implicits"*/)

sourcesInBase in ThisBuild := false

lazy val root = (project in file(".")).aggregate(sales, shipping, invoicing, `e2e-tests`)

lazy val sales = project
lazy val invoicing = project
lazy val shipping = project

lazy val `e2e-tests` = project
