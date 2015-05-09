import Deps._
import sbt.Keys._

lazy val invoicing = (project in file(".")).aggregate(`invoicing-contracts`, `invoicing-write-back`, `invoicing-write-front`)

lazy val `invoicing-contracts` = (project in file("contracts"))
  .settings(
    libraryDependencies ++= Seq(
      AkkaDDD.messaging
    ) ++ Json.`4s`
  ).dependsOn("sales-contracts")

lazy val `invoicing-write-back` = (project in file("write-back"))
  .settings(
    libraryDependencies ++= Seq(
      Akka.kernel, Akka.testkit,
      AkkaDDD.messaging, AkkaDDD.core, AkkaDDD.scheduling, AkkaDDD.test,
      AkkaDDD.eventStore, Eventstore.akkaJournal
    )
  )
  .dependsOn(`invoicing-contracts`)

lazy val `invoicing-write-front` = (project in file("write-front"))
  .settings(
    libraryDependencies ++= SqlDb() ++ Seq(
      AkkaDDD.writeFront, Akka.kernel, Akka.slf4j
    )
  )
  .dependsOn(`invoicing-contracts`)
