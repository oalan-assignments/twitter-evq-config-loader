name := "twitter-evq-config-loader"
version := "0.1"
scalaVersion := "2.13.5"

libraryDependencies ++= Seq(
"org.scalatest" %% "scalatest" % "3.2.7" % "test"
)

parallelExecution in Test := false
