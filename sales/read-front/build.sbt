enablePlugins(JavaAppPackaging)
mainClass in Compile := Some("akka.kernel.Main ecommerce.sales.app.SalesReadFrontApp")

dockerExposedPorts := Seq(9110)