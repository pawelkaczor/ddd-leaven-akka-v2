import Deps._
import sbt.Keys._

lazy val shipping = (project in file(".")).aggregate(`shipping-contracts`, `shipping-write-back`, `shipping-read-back`, `shipping-read-front`)

lazy val `shipping-contracts` = (project in file("contracts"))
  .settings(
    libraryDependencies ++= Seq(
      AkkaDDD.messaging
    ) ++ Json.`4s`
  )
  .dependsOn("invoicing-contracts")

lazy val `shipping-write-back` = (project in file("write-back"))
  .settings(
    libraryDependencies ++= Seq(
      Akka.testkit,
      AkkaDDD.messaging, AkkaDDD.core, AkkaDDD.test, AkkaDDD.eventStore
    )
  )
  .dependsOn(`shipping-contracts`, "commons")

lazy val `shipping-read-back` = (project in file("read-back"))
  .settings(
    parallelExecution in Test := false,
    libraryDependencies ++= SqlDb() ++ Seq(
      AkkaDDD.viewUpdateSql
    )
  )
  .dependsOn(`shipping-contracts`, "commons")

lazy val `shipping-read-front` = (project in file("read-front"))
  .settings(
    parallelExecution in Test := false,
    libraryDependencies ++= AkkaDDD.httpSupport ++ Seq(
      Akka.testkit
    )
  )
  .dependsOn(`shipping-read-back` % "test->test;compile->compile", "commons")
