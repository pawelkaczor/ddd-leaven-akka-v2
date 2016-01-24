import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import sbt.Keys._
import sbt._
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._

object MonitoringPlugin extends AutoPlugin {

  override def requires = JavaAppPackaging

  override lazy val projectSettings = Seq(
    libraryDependencies += (projectID in LocalProject("monitoring")).value,
    javaOptions in Universal ++= Seq(
      "-DmonitoringRunnerClass=ecommerce.monitoring.MonitoringRunner",
      s"-Dapp.name=${(name in thisProject).value}"
    )
  )

}