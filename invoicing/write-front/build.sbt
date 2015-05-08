enablePlugins(JavaAppPackaging)
mainClass in Compile := Some("akka.kernel.Main ecommerce.invoicing.app.InvoicingFrontApp")

dockerExposedPorts := Seq(9200)