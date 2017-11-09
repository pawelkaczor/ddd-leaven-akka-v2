import Deps._
import sbt.Keys._

lazy val invoicing = (project in file(".")).aggregate(`invoicing-contracts`, `invoicing-write-back`, `invoicing-write-front`)

lazy val `invoicing-contracts` = (project in file("contracts"))
  .settings(
    libraryDependencies += AkkaDDD.protocol
  ).dependsOn(lp("sales-contracts"))



lazy val `invoicing-write-back` = (project in file("write-back"))
  .settings(
    dockerExposedPorts := Seq(9201),
    javaOptions in Universal ++= Seq("-DmainClass=ecommerce.invoicing.app.InvoicingBackendApp"),
    libraryDependencies ++=
      Seq(AkkaDDD.core, AkkaDDD.scheduling, AkkaDDD.test, AkkaDDD.eventStore, AkkaDDD.monitoring)
  )
  .dependsOn(`invoicing-contracts`, lp("shipping-contracts"), lp("commons"), lp("headquarters-event-tagging"))
  .enablePlugins(WriteBackPlugin)



lazy val `invoicing-write-front` = (project in file("write-front"))
  .settings(
    dockerExposedPorts := Seq(9200),
    javaOptions in Universal ++= Seq("-DmainClass=ecommerce.invoicing.app.InvoicingFrontApp"),
    libraryDependencies += AkkaDDD.writeFront
  )
  .dependsOn(`invoicing-contracts`, lp("commons"))
  .enablePlugins(HttpServerPlugin)
