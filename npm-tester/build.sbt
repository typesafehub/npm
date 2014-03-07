resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.mavenLocal,
  "Typesafe Snapshots Repository" at "http://repo.typesafe.com/typesafe/snapshots/"
)

libraryDependencies ++= Seq(
  "io.apigee.trireme" % "trireme-core" % "0.7.1-SNAPSHOT",
  "org.slf4j" % "slf4j-simple" % "1.7.6"
)

fork in run := true

//javaOptions := Seq("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005")
//javaOptions := Seq("-Dorg.slf4j.simpleLogger.defaultLogLevel=debug")