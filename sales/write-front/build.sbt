enablePlugins(JavaAppPackaging)

mainClass in Compile := Some("akka.kernel.Main ecommerce.sales.app.SalesFrontApp")

dockerExposedPorts := Seq(9100)

