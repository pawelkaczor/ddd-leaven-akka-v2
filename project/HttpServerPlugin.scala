import sbt.Keys._
import sbt._
import Deps._

object HttpServerPlugin extends AutoPlugin {

  override def requires = ApplicationPlugin

  override lazy val projectSettings = Seq(
    libraryDependencies ++= AkkaDDD.httpSupport ++ Seq(Akka.httpHealth),
    parallelExecution in Test := false
  )
}