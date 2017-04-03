name := "swapi-finch"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.github.finagle" %% "finch-core" % "0.14.0",
  "com.github.finagle" %% "finch-jackson" % "0.14.0"
)
