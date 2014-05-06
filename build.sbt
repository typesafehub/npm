organization := "com.typesafe"

name := "npm"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "com.typesafe" %% "jse" % "1.0.0-RC1",
  "org.webjars" % "npm" % "1.3.26",
  "com.typesafe.akka" %% "akka-actor" % "2.3.2",
  "org.webjars" % "webjars-locator" % "0.14",
  "org.specs2" %% "specs2" % "2.3.11" % "test",
  "junit" % "junit" % "4.11" % "test"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.mavenLocal,
  "Typesafe Releases Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Typesafe Snapshots Repository" at "http://repo.typesafe.com/typesafe/snapshots/"
)

publishTo := {
    val typesafe = "http://private-repo.typesafe.com/typesafe/"
    val (name, url) = if (isSnapshot.value)
                        ("sbt-plugin-snapshots", typesafe + "maven-snapshots")
                      else
                        ("sbt-plugin-releases", typesafe + "maven-releases")
    Some(Resolver.url(name, new URL(url)))
}

lazy val root = project in file(".")

lazy val `npm-tester` = project.dependsOn(root)
