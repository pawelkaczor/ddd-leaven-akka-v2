enablePlugins(JavaAppPackaging)
mainClass in Compile := Some("akka.kernel.Main")

dockerExposedPorts := Seq(9110)