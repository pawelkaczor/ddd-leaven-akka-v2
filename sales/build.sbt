import Deps._
import com.typesafe.sbt.SbtMultiJvm
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm
import sbt.Keys._

lazy val sales = (project in file(".")).aggregate(`sales-contracts`, `sales-write-back`, `sales-write-front`, `sales-read-back`, `sales-read-front`)

lazy val `sales-contracts` = (project in file("contracts"))
  .settings(
    libraryDependencies ++= Seq(
      AkkaDDD.messaging
    ) ++ Json.`4s`
  )

lazy val `sales-write-back` = (project in file("write-back"))
  .settings(
      multiNodeTestingSettings,
      libraryDependencies ++= Seq(
        Akka.kernel, Akka.testkit,
        AkkaDDD.messaging, AkkaDDD.core, AkkaDDD.test, AkkaDDD.eventStore
      )
    )
    .dependsOn(`sales-contracts`, "invoicing-contracts")
    .configs(MultiJvm)

lazy val `sales-write-front` = (project in file("write-front"))
  .settings(
    libraryDependencies ++= SqlDb() ++ Seq(
      AkkaDDD.writeFront, Akka.kernel, Akka.slf4j
    )
  )
  .dependsOn(`sales-contracts`)

lazy val `sales-read-back` = (project in file("read-back"))
  .settings(
    parallelExecution in Test := false,
    libraryDependencies ++= SqlDb() ++ Seq(
      AkkaDDD.viewUpdateSql,
      Akka.kernel
    )
  )
  .dependsOn(`sales-contracts`)

lazy val `sales-read-front` = (project in file("read-front"))
  .settings(
    parallelExecution in Test := false,
    libraryDependencies ++= AkkaDDD.httpSupport ++ Seq(
      Akka.testkit
    )
  )
  .dependsOn(`sales-read-back` % "test->test;compile->compile")

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
