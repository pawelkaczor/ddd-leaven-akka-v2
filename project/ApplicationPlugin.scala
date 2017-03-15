import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.archetypes.scripts.AshScriptPlugin
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._
import sbt.Keys._
import sbt._

object ApplicationPlugin extends AutoPlugin {

  override def requires = CommonSettingsPlugin && JavaAppPackaging && AshScriptPlugin

  override lazy val projectSettings = Seq(
    mainClass in Compile := Some("akka.kernel.Main"),
    javaOptions in Universal += "-server"
  )
}