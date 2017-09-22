import Deps._
import sbt.Keys._

lazy val shipping = (project in file(".")).aggregate(`shipping-contracts`, `shipping-write-back`, `shipping-read-back`, `shipping-read-front`)

lazy val `shipping-contracts` = (project in file("contracts"))
  .settings(
    libraryDependencies += AkkaDDD.messaging
  )
  .dependsOn(lp("invoicing-contracts"))



lazy val `shipping-write-back` = (project in file("write-back"))
  .settings(
    dockerExposedPorts := Seq(9301),
    javaOptions in Universal ++= Seq("-DmainClass=ecommerce.shipping.app.ShippingBackendApp"),
    libraryDependencies ++=
      Seq(AkkaDDD.core, AkkaDDD.test, AkkaDDD.eventStore, AkkaDDD.scheduling)
  )
  .dependsOn(`shipping-contracts`, lp("sales-contracts"), lp("commons"), lp("headquarters-event-tagging"))
  .enablePlugins(WriteBackPlugin)



lazy val `shipping-read-back` = (project in file("read-back"))
  .settings(
    javaOptions in Universal ++= Seq("-DmainClass=ecommerce.shipping.app.ShippingViewUpdateApp"),
    libraryDependencies ++= AkkaDDD.viewUpdateSql ++ Seq(AkkaDDD.eventStore)
  )
  .dependsOn(`shipping-contracts`, lp("commons"))
  .enablePlugins(ApplicationPlugin)



lazy val `shipping-read-front` = (project in file("read-front"))
  .settings(
    dockerExposedPorts := Seq(9310),
    javaOptions in Universal ++= Seq("-DmainClass=ecommerce.shipping.app.ShippingReadFrontApp")
  )
  .dependsOn(`shipping-read-back` % "test->test;compile->compile", lp("commons"))
  .enablePlugins(HttpServerPlugin)
