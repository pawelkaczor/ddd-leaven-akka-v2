logLevel := Level.Info

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.sbt" % "sbt-multi-jvm" % "0.3.8")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "0.8.0-RC1")

addSbtPlugin("com.orrsella" % "sbt-stats" % "1.0.5")