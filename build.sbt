organization := "com.typesafe"

name := "npm"

version := "1.0.0-M2"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "com.typesafe" %% "jse" % "1.0.0-M2",
  "org.webjars" % "npm" % "1.3.26",
  "com.typesafe.akka" %% "akka-actor" % "2.2.3",
  "org.webjars" % "webjars-locator" % "0.9",
  "org.specs2" %% "specs2" % "2.2.2" % "test",
  "junit" % "junit" % "4.11" % "test"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.mavenLocal,
  "Typesafe Releases Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Typesafe Snapshots Repository" at "http://repo.typesafe.com/typesafe/snapshots/"
)

// FIXME: Working around https://github.com/sbt/sbt/issues/1156#issuecomment-39317363
isSnapshot := true

publishTo := {
    val isSnapshot = version.value.contains("-SNAPSHOT")
    val typesafe = "http://private-repo.typesafe.com/typesafe/"
    val (name, url) = if (isSnapshot)
                        ("sbt-plugin-snapshots", typesafe + "maven-snapshots")
                      else
                        ("sbt-plugin-releases", typesafe + "maven-releases")
    Some(Resolver.url(name, new URL(url)))
}

lazy val root = project.in( file(".") )

lazy val `npm-tester` = project.dependsOn(root)
