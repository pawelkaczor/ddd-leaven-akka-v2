import sbt._
import Keys._
import com.typesafe.sbt.packager.docker.{Cmd, DockerKeys}
import RestartCommand._

object CommonSettingsPlugin extends AutoPlugin with DockerKeys {
  override def trigger = allRequirements
  override lazy val projectSettings = Seq(
    updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true),
    libraryDependencies ++= Seq(
      "com.github.nscala-time" %% "nscala-time" % "2.2.0",
      "ch.qos.logback" % "logback-classic" % "1.1.3",
      "org.scalatest" %% "scalatest" % "2.2.4" % "test",
      "org.mockito" % "mockito-core" % "1.9.5" % "test",
      "commons-io" % "commons-io" % "2.4" % "test",
      "org.scalacheck" %% "scalacheck" % "1.12.5" % "test"
    ),
    commands ++= Seq(restart)
  )
}