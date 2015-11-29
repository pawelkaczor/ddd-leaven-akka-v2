import com.typesafe.sbt.packager.archetypes.{AshScriptPlugin, JavaAppPackaging}
import sbt.Keys._
import sbt._

object ApplicationPlugin extends AutoPlugin {

  override def requires = CommonSettingsPlugin && JavaAppPackaging && AshScriptPlugin

  override lazy val projectSettings = Seq(
    javaOptions += "-server",
    mainClass in Compile := Some("akka.kernel.Main")
  )
}