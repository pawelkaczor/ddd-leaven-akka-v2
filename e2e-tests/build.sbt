import sbt._
import sbt.Keys._
import Vagrant._
import E2EConfig._

lazy val `e2e-tests` = (project in file("."))
  .settings(e2eTestingSettings ++ Vagrant.settings)
  .settings(
    vagrantFile := baseDirectory.value / "Vagrantfile",
    libraryDependencies ++= Seq(
      "org.yaml" % "snakeyaml" % "1.14",
      "pl.newicom" %% "resttest" % "0.3.0-SNAPSHOT"
    )
  )
  .configs(E2ETest)
  .dependsOn("sales-contracts", "invoicing-contracts", "shipping-contracts")

