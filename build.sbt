import sbt.Keys._

organization in ThisBuild := "pl.newicom"

version in ThisBuild := "0.2-M2"

scalaVersion in ThisBuild := "2.11.6"

scalacOptions in ThisBuild := Seq("-encoding", "utf8", "-feature", "-language:postfixOps", "-language:implicitConversions"/*, "-Xlog-implicits"*/)

sourcesInBase in ThisBuild := false

lazy val root = project.settings(
    aggregate in update := false
  )
  .aggregate(sales, shipping, invoicing, `e2e-tests`)

lazy val sales = project
lazy val invoicing = project
lazy val shipping = project

lazy val `e2e-tests` = project

addCommandAlias("redeploy", ";stage;docker:stage;restart")

