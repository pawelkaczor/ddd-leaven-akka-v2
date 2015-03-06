import Deps._
import com.typesafe.sbt.SbtMultiJvm
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm
import sbt.Keys._

name := "ddd-leaven-akka-v2"

organization in ThisBuild := "pl.newicom"

scalaVersion in ThisBuild := "2.11.5"

scalacOptions in ThisBuild := Seq("-encoding", "utf8", "-feature", "-language:postfixOps", "-language:implicitConversions"/*, "-Xlog-implicits"*/)

resolvers in ThisBuild += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

sourcesInBase := false

lazy val root = (project in file("."))
  .settings(
    updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true)
  )
  .aggregate(`sales-contracts`, `sales-write-back`, `sales-write-front`, `sales-read-back`, `sales-read-front`)

//
// Sales subsystem
//
lazy val `sales-contracts` = project
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      AkkaDDD.messaging
    ) ++ Json.`4s`
  )

lazy val `sales-write-back` = project
  .settings(commonSettings ++ multiNodeTestingSettings: _*)
  .settings(
      libraryDependencies ++= Seq(
        Akka.kernel, Akka.testkit,
        AkkaDDD.messaging, AkkaDDD.core, AkkaDDD.test,
        AkkaDDD.eventStore
      )
    )
    .dependsOn(`sales-contracts`)
    .configs(MultiJvm)

lazy val `sales-write-front` = project
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= SqlDb() ++ Seq(
      AkkaDDD.writeFront, Akka.kernel, Akka.slf4j
    )
  )
  .dependsOn(`sales-contracts`)

lazy val `sales-read-back` = project
  .settings(commonSettings: _*)
  .settings(
    parallelExecution in Test := false,
    libraryDependencies ++= SqlDb() ++ Seq(
      AkkaDDD.viewUpdateSql,
      Akka.kernel
    )
  )
  .dependsOn(`sales-contracts`)

lazy val `sales-read-front` = project
  .settings(commonSettings: _*)
  .settings(
    parallelExecution in Test := false,
    libraryDependencies ++= Seq(
      Akka.testkit, Akka.httpTestKit, AkkaDDD.httpSupport
    )
  )
  .dependsOn(`sales-read-back` % "test->test;compile->compile")

//
// Invoicing subsystem
//
lazy val `invoicing-write-back` = project
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      Akka.kernel, Akka.testkit,
      AkkaDDD.messaging, AkkaDDD.core, AkkaDDD.test,
      AkkaDDD.eventStore, Eventstore.akkaJournal
    )
  )
  .dependsOn(`sales-contracts`)

//
// Shipping subsystem
//
lazy val `shipping-contracts` = project
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      AkkaDDD.messaging
    ) ++ Json.`4s`
  )
  .dependsOn(`sales-contracts`)

lazy val `shipping-write-back` = project
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      Akka.kernel, Akka.testkit,
      AkkaDDD.messaging, AkkaDDD.core, AkkaDDD.test,
      AkkaDDD.eventStore
    )
  )
  .dependsOn(`shipping-contracts`)

lazy val `shipping-read-back` = project
  .settings(commonSettings: _*)
  .settings(
    parallelExecution in Test := false,
    libraryDependencies ++= SqlDb() ++ Seq(
      AkkaDDD.viewUpdateSql,
      Akka.kernel
    )
  )
  .dependsOn(`shipping-contracts`)

lazy val `shipping-read-front` = project
  .settings(commonSettings: _*)
  .settings(
    parallelExecution in Test := false,
    libraryDependencies ++= Seq(
      Akka.testkit, Akka.httpTestKit, AkkaDDD.httpSupport
    )
  )
  .dependsOn(`shipping-read-back` % "test->test;compile->compile")

lazy val commonSettings: Seq[Setting[_]] = Seq(
  resolvers ++= Seq("Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"),
  libraryDependencies ++= Seq(
    "com.github.nscala-time" %% "nscala-time" % "1.4.0",
    "ch.qos.logback" % "logback-classic" % "1.1.2",
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    "org.mockito" % "mockito-core" % "1.9.5" % "test",
    "commons-io" % "commons-io" % "2.4" % "test",
    "org.scalacheck" %% "scalacheck" % "1.11.6" % "test"
  )
)

lazy val multiNodeTestingSettings: Seq[Setting[_]] = SbtMultiJvm.multiJvmSettings ++ Seq(
  // make sure that MultiJvm test are compiled by the default test compilation
  compile in MultiJvm <<= (compile in MultiJvm) triggeredBy (compile in Test),
  // disable parallel tests
  parallelExecution in Test := false,
  // make sure that MultiJvm tests are executed by the default test target,
  // and combine the results from ordinary test and multi-jvm tests
  executeTests in Test <<= (executeTests in Test, executeTests in MultiJvm) map {
    case (testResults, multiNodeResults)  =>
      val overall =
        if (testResults.overall.id < multiNodeResults.overall.id)
          multiNodeResults.overall
        else
          testResults.overall
      Tests.Output(overall,
        testResults.events ++ multiNodeResults.events,
        testResults.summaries ++ multiNodeResults.summaries)
  },
  libraryDependencies ++= Seq(
    Akka.multiNodeTestkit
  )
)
