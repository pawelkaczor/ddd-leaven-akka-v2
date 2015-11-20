import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import sbt.Keys._
import sbt._

object ApplicationPlugin extends AutoPlugin {

  override def requires = CommonSettingsPlugin && JavaAppPackaging

  override lazy val projectSettings = Seq(
    javaOptions += "-server",
    mainClass in Compile := Some("akka.kernel.Main")
  )
}