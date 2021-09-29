name := "twitter-evq-config-loader"
version := "0.1"
scalaVersion := "2.13.5"
val AkkaVersion = "2.6.16"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.7" % "test",
  "com.lihaoyi" %% "os-lib" % "0.7.3",
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test,
  "com.google.guava" % "guava" % "28.1-jre",
  "org.slf4j" % "slf4j-api" % "1.7.32" % "test",
  "ch.qos.logback" % "logback-core" % "1.2.6",
  "ch.qos.logback" % "logback-classic" % "1.2.6"
)

parallelExecution in Test := false
