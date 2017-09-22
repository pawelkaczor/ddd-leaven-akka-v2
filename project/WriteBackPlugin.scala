import sbt.Keys._
import sbt._

object WriteBackPlugin extends AutoPlugin {

  override def requires = ApplicationPlugin

  override lazy val projectSettings = Seq(
    libraryDependencies += "com.lightbend.akka" %% "akka-management-cluster-http" % "0.4"
  )
}