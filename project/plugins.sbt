logLevel := Level.Info

resolvers ++= Seq("Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/")

addSbtPlugin("com.typesafe.sbt" % "sbt-multi-jvm" % "0.3.11")

addSbtPlugin("com.orrsella" % "sbt-stats" % "1.0.5")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.0.3")


