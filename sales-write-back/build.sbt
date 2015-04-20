enablePlugins(JavaAppPackaging)

mainClass in Compile := Some("akka.kernel.Main ecommerce.sales.app.SalesBackendApp")

dockerExposedPorts := Seq(9101)