organization := "com.typesafe"
name := "npm"

scalaVersion := "2.10.7"
crossScalaVersions := Seq(scalaVersion.value, "2.11.12", "2.12.13")

libraryDependencies ++= {
  val akkaVersion = scalaBinaryVersion.value match {
    case "2.10" => "2.3.16"
    case "2.11" => "2.5.26"
    case "2.12" => "2.6.14"
  }
  Seq(
    "com.typesafe" %% "jse" % "1.2.4",
	// workaround to resolve dependency resolution error
    "org.webjars.npm" % "lodash._baseindexof" % "3.1.0",
    "org.webjars.npm" % "lodash._cacheindexof" % "3.0.2",
    "org.webjars.npm" % "lodash._getnative" % "3.9.1",
	// for npm 6.14.11
	"org.webjars.npm" % "lodash._bindcallback" % "3.0.1",
	"org.webjars.npm" % "lodash.restparam" % "3.6.1",
	"org.webjars.npm" % "lodash._createcache" % "3.1.2",
	"org.webjars.npm" % "debuglog" % "1.0.1",
	"org.webjars.npm" % "imurmurhash" % "0.1.4",
	// end of workaround
    "org.webjars.npm" % "npm" % "6.14.11",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "org.webjars" % "webjars-locator-core" % "0.43",
    "commons-io" % "commons-io" % "2.6" % "test",
    "org.specs2" %% "specs2-core" % "3.10.0" % "test",
    "junit" % "junit" % "4.12" % "test"
  )
}

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

xerial.sbt.Sonatype.SonatypeKeys.sonatypeProfileName := "com.typesafe"

// Release settings

releaseCrossBuild := true
releasePublishArtifactsAction := PgpKeys.publishSigned.value
releaseTagName := (version in ThisBuild).value

import ReleaseTransformations._

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  releaseStepCommand("sonatypeRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
