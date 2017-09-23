import Deps._
import sbt.Keys._
import com.typesafe.sbt.packager.docker.Cmd

lazy val headquarters = (project in file("."))
  .aggregate(`headquarters-event-tagging`, `headquarters-write-back`)

lazy val `headquarters-event-tagging` = (project in file("event-tagging"))
  .settings(
    libraryDependencies ++=
      Seq(AkkaDDD.core)
  )

lazy val `headquarters-write-back` = (project in file("write-back"))
  .settings(
    dockerExposedPorts := Seq(9401),
    javaOptions in Universal += "-DmainClass=ecommerce.headquarters.app.HeadquartersApp",
    libraryDependencies ++= Seq(AkkaDDD.core, AkkaDDD.test, AkkaDDD.eventStore, AkkaDDD.scheduling),
    dockerCommands ++= Seq(
      Cmd("HEALTHCHECK", "--interval=10s --timeout=2s CMD curl -sf http://127.0.0.1:19999/members/akka.tcp://ecommerce@127.0.0.1:9401 | jq -r '.status' | grep Up")
    )

  )
  .dependsOn(lp("sales-contracts"), lp("invoicing-contracts"), lp("shipping-contracts"), lp("commons"))
  .enablePlugins(WriteBackPlugin)