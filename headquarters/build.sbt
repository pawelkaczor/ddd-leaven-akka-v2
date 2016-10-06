import Deps._
import sbt.Keys._

lazy val headquarters = (project in file(".")).aggregate(`headquarters-write-back`)

lazy val `headquarters-write-back` = (project in file("write-back"))
  .settings(
    dockerExposedPorts := Seq(9401),
    javaOptions in Universal += "-DmainClass=ecommerce.headquarters.app.HeadquartersApp",
    libraryDependencies ++=
      Seq(AkkaDDD.core, AkkaDDD.test, AkkaDDD.eventStore, AkkaDDD.scheduling)
  )
  .dependsOn("sales-contracts", "invoicing-contracts", "shipping-contracts", "commons")
  .enablePlugins(ApplicationPlugin)