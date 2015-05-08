enablePlugins(JavaAppPackaging)
mainClass in Compile := Some("akka.kernel.Main ecommerce.invoicing.app.InvoicingBackendApp")

dockerExposedPorts := Seq(9201)