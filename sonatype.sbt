pomExtra in Global := {
  <url>github.com/CatalystCode/SpeechToText-Websockets-Java</url>
    <licenses>
      <license>
        <name>MIT</name>
        <url>https://opensource.org/licenses/MIT</url>
      </license>
    </licenses>
    <scm>
      <connection>scm:git:github.com/CatalystCode/SpeechToText-Websockets-Java</connection>
      <developerConnection>scm:git:git@github.com:CatalystCode/SpeechToText-Websockets-Java</developerConnection>
      <url>github.com/CatalystCode/SpeechToText-Websockets-Java</url>
    </scm>
    <developers>
      <developer>
        <id>c-w</id>
        <name>Clemens Wolff</name>
        <email>clewolff@microsoft.com</email>
        <url>http://github.com/c-w</url>
      </developer>
    </developers>
}

credentials += Credentials(
  "Sonatype Nexus Repository Manager",
  "oss.sonatype.org",
  System.getenv("SONATYPE_USER"),
  System.getenv("SONATYPE_PASSWORD"))

organizationName := "Partner Catalyst"
organizationHomepage := Some(url("https://github.com/CatalystCode"))

publishTo := {
  val isSnapshot = version.value.trim.endsWith("SNAPSHOT")
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot) Some("snapshots" at nexus + "content/repositories/snapshots")
  else            Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true
publishArtifact in Test := false
useGpg := true
