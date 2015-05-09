
import sbt._
import sbt.Keys._
import scala.language.postfixOps
import E2EConfig._

object Vagrant {

  lazy val vagrantFile = settingKey[File]("vagrant-file")
  private lazy val vagrant = settingKey[Vagrant]("vagrant")

  lazy val settings = Seq(
    vagrant := new Vagrant(vagrantFile.value),
    testOptions in E2ETest ++= Seq(
      Tests.Setup(()   => if ((definedTests in E2ETest).value.nonEmpty) vagrant.value.setup()),
      Tests.Cleanup(() => if ((definedTests in E2ETest).value.nonEmpty) vagrant.value.cleanup())
    )
  )
}

class Vagrant(vagrantFile: File) {

  private var prevStatus: VagrantStatus = Unknown
  private val vagrantDir = vagrantFile.getParentFile

  def setup(): Unit = {
    prevStatus = status()
    prevStatus match {
      case _  => up()
    }
  }

  def cleanup(): Unit = if (prevStatus != Running) destroy()

  // cli method wrappers
  private def status(): VagrantStatus = {
    val res = Process("vagrant" :: "status" :: Nil, vagrantFile.getParentFile)!!

    if (res.contains("running (")) Running
    else if (res.contains("saved (")) Saved
    else if (res.contains("not created (")) NotCreated
    else Unknown
  }

  private def up(): Unit = Process("vagrant" :: "up" :: Nil, vagrantDir)!
  private def reload(): Unit = Process("vagrant" :: "reload" :: Nil, vagrantDir)!
  private def provision(): Unit = Process("vagrant" :: "provision" :: Nil, vagrantDir)!
  private def suspend(): Unit = Process("vagrant" :: "suspend" :: Nil, vagrantDir)!
  private def halt(): Unit = Process("vagrant" :: "halt" :: Nil, vagrantDir)!
  private def destroy(): Unit = Process("vagrant" :: "destroy" :: "-f" :: Nil, vagrantDir)!

  sealed trait VagrantStatus
  case object Running extends VagrantStatus
  case object Saved extends VagrantStatus
  case object NotCreated extends VagrantStatus
  case object Unknown extends VagrantStatus
}
