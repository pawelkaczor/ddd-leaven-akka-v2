import sbt._
import Keys._
import scala.sys.process._

object RestartCommand {

  def restart = Command.command("restart") { state =>
    val stateExtracted = Project.extract(state)
    doRestart(stateExtracted.currentProject.id)
    state
  }

  private def doRestart(appName: String): Int = {
    if (appName == "ddd-leaven-akka-v2" || appName == "root") {
      println(s"Restarting all applications")
      "./restart" !
    } else {
      println(s"Restarting: ${appName}")
      s"./restart $appName" !
    }
  }
}