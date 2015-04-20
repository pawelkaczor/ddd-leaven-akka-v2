import sbt._
import sbt.Keys._
import scala.language.postfixOps

object Vagrant {

  import BuildKeys._

  private lazy val vagrant = settingKey[Vagrant]("vagrant")

  lazy val settings = Seq(
    test <<= ((test in Test).dependsOn(publishLocal)),
    vagrant := new Vagrant(vagrantFile.value),
    testOptions += Tests.Setup(()   => if ((definedTests in Test).value.nonEmpty) vagrant.value.setup()),
    testOptions += Tests.Cleanup(() => if ((definedTests in Test).value.nonEmpty) vagrant.value.cleanup())
  )
}

class Vagrant(vagrantFile: File) {

  private var prevStatus: VagrantStatus = Unknown
  private val vagrantDir = vagrantFile.getParentFile

  def setup(): Unit = {
    prevStatus = status()
    prevStatus match {
      case Running    => provision()
      case Saved      => up(); provision()
      case NotCreated => up()
      case Unknown    => up()
    }
  }

  def cleanup(): Unit = if (prevStatus != Running) suspend()

  // cli method wrappers
  private def status(): VagrantStatus = {
    val res = Process("vagrant" :: "status" :: Nil, vagrantFile.getParentFile)!!

    if (res.contains("running (")) Running
    else if (res.contains("saved (")) Saved
    else if (res.contains("not created (")) NotCreated
    else Unknown
  }

  private def up(): Unit = Process("vagrant" :: "up" :: Nil, vagrantDir)!
  private def provision(): Unit = Process("vagrant" :: "provision" :: Nil, vagrantDir)!
  private def suspend(): Unit = Process("vagrant" :: "suspend" :: Nil, vagrantDir)!
  private def destroy(): Unit = Process("vagrant" :: "destroy" :: "-f" :: Nil, vagrantDir)!

  sealed trait VagrantStatus
  case object Running extends VagrantStatus
  case object Saved extends VagrantStatus
  case object NotCreated extends VagrantStatus
  case object Unknown extends VagrantStatus
}
