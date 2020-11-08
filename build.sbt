name := "metadata-extractor-3000"
version := "0.0.1"
scalaVersion := "2.13.3"

//Logger
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

//AWS SDK
libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.11.896"

//Better Files
libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.9.1"

//JSON
val circeVersion = "0.12.3"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)
