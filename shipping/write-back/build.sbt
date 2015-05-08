enablePlugins(JavaAppPackaging)
mainClass in Compile := Some("akka.kernel.Main ecommerce.shipping.app.ShippingBackendApp")

dockerExposedPorts := Seq(9301)