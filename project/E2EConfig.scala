import sbt.Keys._
import sbt._

object E2EConfig {
  lazy val e2eTestingSettings: Seq[Setting[_]] = {

    def e2eFilter(name: String): Boolean = (name endsWith "SystemSpec") || (name endsWith "E2ESpec")
    def unitFilter(name: String): Boolean = ((name endsWith "Test") || (name endsWith "Spec")) && !e2eFilter(name)

    inConfig(E2ETest)(Defaults.testTasks) ++ Seq(
      fork in E2ETest := true,
      testOptions in E2ETest := Seq(Tests.Filter(e2eFilter)),
      testOptions in Test := Seq(Tests.Filter(unitFilter))
    )
  }

  lazy val E2ETest = config("e2e") extend Test

}