import sbt._
import sbt.Keys._

import scala.language.postfixOps
import scala.sys.process._
import E2EConfig._

object Vagrant {

  lazy val vagrantFile              = settingKey[File]("vagrant-file")
  lazy val vagrantContainersLogFile = settingKey[File]("vagrant-containers-log-file")
  lazy val vagrant                  = settingKey[Vagrant]("vagrant")

  lazy val settings = Seq(
    vagrant := new Vagrant(vagrantFile.value),
    testOptions in E2ETest ++= Seq(
      Def.taskDyn {
        if ((definedTests in E2ETest).value.nonEmpty) {
          val vv = vagrant.value
          Def.task { Tests.Setup(() => vv.setup()) }
        } else {
          Def.task { Tests.Setup(() => ()) }
        }
      }.value,
      Def.taskDyn {
        if ((definedTests in E2ETest).value.nonEmpty) {
          val vv           = vagrant.value
          val logFileValue = vagrantContainersLogFile.value
          Def.task {
            Tests.Cleanup(() => {
              vv.collectLogs(logFileValue)
              vv.cleanup()
            })
          }
        } else {
          Def.task { Tests.Cleanup(() => ()) }
        }
      }.value
    )
  )
}

class Vagrant(vagrantFile: File) {

  private var prevStatus: VagrantStatus = Unknown
  private val vagrantDir                = vagrantFile.getParentFile

  def setup(): Unit = {
    prevStatus = status()
    prevStatus match {
      case _ => up()
    }
  }

  def cleanup(): Unit = if (prevStatus != Running) destroy()

  // cli method wrappers
  private def status(): VagrantStatus = {
    val res: String = Process("vagrant" :: "status" :: Nil, vagrantFile.getParentFile) !!

    if (res.contains("running (")) Running
    else if (res.contains("saved (")) Saved
    else if (res.contains("not created (")) NotCreated
    else Unknown
  }

  private def up(): Unit                       = Process("vagrant" :: "up" :: Nil, vagrantDir) !
  private def collectLogs(logFile: File): Unit = Process("vagrant" :: "docker-logs" :: Nil, vagrantDir) #> logFile !
  private def reload(): Unit                   = Process("vagrant" :: "reload" :: Nil, vagrantDir) !
  private def provision(): Unit                = Process("vagrant" :: "provision" :: Nil, vagrantDir) !
  private def suspend(): Unit                  = Process("vagrant" :: "suspend" :: Nil, vagrantDir) !
  private def halt(): Unit                     = Process("vagrant" :: "halt" :: Nil, vagrantDir) !
  private def destroy(): Unit                  = Process("vagrant" :: "destroy" :: "-f" :: Nil, vagrantDir) !

  sealed trait VagrantStatus
  case object Running    extends VagrantStatus
  case object Saved      extends VagrantStatus
  case object NotCreated extends VagrantStatus
  case object Unknown    extends VagrantStatus
}
