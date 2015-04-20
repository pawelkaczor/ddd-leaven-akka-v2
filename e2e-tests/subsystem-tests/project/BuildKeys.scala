import sbt._

object BuildKeys {

  lazy val testAll = TaskKey[Unit]("test-all")
  lazy val vagrantFile = SettingKey[File]("vagrant-file")
}
