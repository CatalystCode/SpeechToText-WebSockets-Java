organization := "com.github.catalystcode"
name := "SpeechToText-WebSockets-Java"
description := "A Java implementation of the Bing Speech to Text API websocket protocol"

javacOptions in (Compile, compile) ++= Seq(
  "-source", "1.8",
  "-target", "1.8")

crossPaths := false
autoScalaLibrary := false

// Bundled dependencies
libraryDependencies ++= Seq(
  "log4j" % "log4j" % "1.2.17",
  "org.json" % "json" % "20170516",
  "com.googlecode.soundlibs" % "jlayer" % "1.0.1-1",
  "com.neovisionaries" % "nv-websocket-client" % "2.2"
)

// Test dependencies
libraryDependencies ++= Seq(
  "org.junit.jupiter" % "junit-jupiter-api" % "5.0.0-M4"
).map(_ % "test")

assemblyMergeStrategy in assembly := {
  case PathList("javax", "inject", xs @ _*) => MergeStrategy.last
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
  case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
  case PathList("org", "aopalliance", xs @ _*) => MergeStrategy.last
  case PathList("org", "apache", xs @ _*) => MergeStrategy.last
  case PathList("com", "google", xs @ _*) => MergeStrategy.last
  case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.last
  case PathList("com", "codahale", xs @ _*) => MergeStrategy.last
  case PathList("com", "yammer", xs @ _*) => MergeStrategy.last
  case "about.html" => MergeStrategy.rename
  case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
  case "META-INF/mailcap" => MergeStrategy.last
  case "META-INF/mimetypes.default" => MergeStrategy.last
  case "plugin.properties" => MergeStrategy.last
  case "log4j.properties" => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
