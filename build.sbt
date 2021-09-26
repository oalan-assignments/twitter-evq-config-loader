name := "twitter-evq-config-loader"
version := "0.1"
scalaVersion := "2.13.5"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.7" % "test",
  "com.lihaoyi" %% "os-lib" % "0.7.3"
)

parallelExecution in Test := false
