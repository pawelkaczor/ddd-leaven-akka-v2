import sbt.Keys._

organization in ThisBuild := "pl.newicom"

version in ThisBuild := "0.2.4-SNAPSHOT"

scalaVersion in ThisBuild := "2.11.7"

scalacOptions in ThisBuild := Seq("-encoding", "utf8", "-feature", "-language:postfixOps", "-language:implicitConversions"/*, "-Xlog-implicits"*/)

sourcesInBase in ThisBuild := false

lazy val root = project.settings(
    aggregate in update := false
  )
  .aggregate(commons, monitoring, sales, shipping, invoicing, `e2e-tests`)

lazy val commons = project
lazy val monitoring = project.dependsOn(commons)

lazy val sales = project.dependsOn(commons)
lazy val invoicing = project.dependsOn(commons)
lazy val shipping = project.dependsOn(commons)

lazy val `e2e-tests` = project

// Rebuilds and restarts current application (or whole system if called from root project)
addCommandAlias("redeploy", ";clean;docker:stage;restart")
addCommandAlias("redeployQuick", ";docker:stage;restart")

