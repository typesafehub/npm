organization := "com.typesafe"
name := "npm"

scalaVersion := "2.10.6"

libraryDependencies ++= Seq(
  "com.typesafe" %% "jse" % "1.2.0",
  "org.webjars" % "npm" % "2.14.14",
  "com.typesafe.akka" %% "akka-actor" % "2.3.16",
  "org.webjars" % "webjars-locator-core" % "0.32",
  "commons-io" % "commons-io" % "2.5" % "test",
  "org.specs2" %% "specs2-core" % "3.8.8" % "test",
  "junit" % "junit" % "4.12" % "test"
)

lazy val root = project in file(".")

lazy val `npm-tester` = project.dependsOn(root)

// Publish settings
publishTo := {
  if (isSnapshot.value) Some(Opts.resolver.sonatypeSnapshots)
  else Some(Opts.resolver.sonatypeStaging)
}
homepage := Some(url("https://github.com/typesafehub/npm"))
licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))
pomExtra := {
  <scm>
    <url>git@github.com:typesafehub/npm.git</url>
    <connection>scm:git:git@github.com:typesafehub/npm.git</connection>
  </scm>
  <developers>
    <developer>
      <id>playframework</id>
      <name>Play Framework Team</name>
      <url>https://github.com/playframework</url>
    </developer>
  </developers>
}
pomIncludeRepository := { _ => false }

// Sonatype settings
sonatypeSettings
SonatypeKeys.profileName := "com.typesafe"

// Release settings
releaseSettings
ReleaseKeys.crossBuild := true
ReleaseKeys.publishArtifactsAction := PgpKeys.publishSigned.value
ReleaseKeys.tagName := (version in ThisBuild).value
ReleaseKeys.releaseProcess += sbtrelease.releaseTask(SonatypeKeys.sonatypeReleaseAll)
