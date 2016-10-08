import sbt.Keys._

organization in ThisBuild := "pl.newicom"

version in ThisBuild := "0.4.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.11.8"

scalacOptions in ThisBuild := Seq("-encoding", "utf8", "-feature", "-language:postfixOps", "-language:implicitConversions"/*, "-Xlog-implicits"*/)

sourcesInBase in ThisBuild := false

lazy val root = project.settings(
    aggregate in update := false
  )
  .aggregate(commons, monitoring, sales, shipping, invoicing, headquarters, `e2e-tests`)

lazy val commons = project
lazy val monitoring = project.dependsOn(commons)

lazy val sales = project.dependsOn(commons)
lazy val invoicing = project.dependsOn(commons)
lazy val shipping = project.dependsOn(commons)

lazy val headquarters = project.dependsOn(commons)

lazy val `e2e-tests` = project

// Rebuilds and restarts current application (or whole system if called from root project)
addCommandAlias("redeploy", ";clean;docker:stage;restart")
addCommandAlias("redeployQuick", ";docker:stage;restart")

// redeployQuick aliases per application
addCommandAlias("rsrb", ";project sales-read-back;docker:stage;restart")
addCommandAlias("rsrf", ";project sales-read-front;docker:stage;restart")
addCommandAlias("rswb", ";project sales-write-back;docker:stage;restart")
addCommandAlias("rswf", ";project sales-write-front;docker:stage;restart")

addCommandAlias("riwb", ";project invoicing-write-back;docker:stage;restart")
addCommandAlias("riwf", ";project invoicing-write-front;docker:stage;restart")

addCommandAlias("rshrb", ";project shipping-read-back;docker:stage;restart")
addCommandAlias("rshrf", ";project shipping-read-front;docker:stage;restart")
addCommandAlias("rshwb", ";project shipping-write-back;docker:stage;restart")

