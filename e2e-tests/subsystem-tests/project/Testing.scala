import sbt._
import sbt.Keys._

object Testing {

  import BuildKeys._

  lazy val settings = Seq(
    fork in Test := false,
    parallelExecution in Test := false
  )

}
