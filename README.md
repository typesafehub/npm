npm
===

[![Build Status](https://travis-ci.org/typesafehub/npm.png?branch=master)](https://travis-ci.org/typesafehub/npm)

This is a JVM wrapper around the [Node Package Manager (NPM)](http://npmjs.org).
The NPM that is run is the real NPM. The [JS Engine project](http://githubc.com/typesafe/js-engine) is
used to execute NPM either using [Trireme](https://github.com/apigee/trireme#trireme)
(a Node API implementation) or [Node](http://nodejs.org) itself.

A [WebJar](http://www.webjars.org/) is used to download the NPM source files.

Some sample usage from Scala:

```scala
    val engine = system.actorOf(Node.props(), "engine")
    val to = new File(new File("target"), "webjars")
    val cacheFile = new File(to, "extraction-cache")
    
    val npm = new Npm(engine, NpmLoader.load(to, cacheFile, Main.getClass.getClassLoader))
    
    for (
      result <- npm.update()  // Perform an "npm update"
      
    ) {
      println(s"status\n======\n${result.exitValue}\n")
      println(s"output\n======\n${new String(result.output.toArray, "UTF-8")}\n")
      println(s"error\n=====\n${new String(result.error.toArray, "UTF-8")}\n")
```
