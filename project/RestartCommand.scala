import sbt._
import Keys._

object RestartCommand {

  def restart = Command.command("restart") { state =>
    val stateExtracted = Project.extract(state)
    doRestart(stateExtracted.currentProject.id)
    state
  }

  private def doRestart(appName: String): Int = {
    println(s"Restarting: ${appName}")

    
    s"./restart $appName" !
  }
}