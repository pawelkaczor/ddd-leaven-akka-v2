import Deps._
import sbt.Keys._

lazy val invoicing = (project in file(".")).aggregate(`invoicing-contracts`, `invoicing-write-back`, `invoicing-write-front`)

lazy val `invoicing-contracts` = (project in file("contracts"))
  .settings(
    libraryDependencies += AkkaDDD.messaging
  ).dependsOn("sales-contracts")



lazy val `invoicing-write-back` = (project in file("write-back"))
  .settings(
    dockerExposedPorts := Seq(9201),
    libraryDependencies ++=
      Seq(AkkaDDD.messaging, AkkaDDD.core, AkkaDDD.scheduling, AkkaDDD.test, AkkaDDD.eventStore)
  )
  .dependsOn(`invoicing-contracts`, "commons")
  .enablePlugins(ApplicationPlugin)



lazy val `invoicing-write-front` = (project in file("write-front"))
  .settings(
    dockerExposedPorts := Seq(9200),
    libraryDependencies += AkkaDDD.writeFront
  )
  .dependsOn(`invoicing-contracts`, "commons")
  .enablePlugins(HttpServerPlugin)
