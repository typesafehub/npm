package com.typesafe.jse

import akka.util.Timeout
import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.npm.{Npm, NpmLoader}
import java.io.File
import org.apache.commons.io.FileUtils
import org.specs2.mutable.Specification
import org.specs2.time.NoTimeConversions
import scala.concurrent.Await
import scala.concurrent.duration._

class NpmSpec extends Specification with NoTimeConversions {

  def withEngine[T](block: ActorRef => T): T = {
    val system = ActorSystem("test-system", classLoader = Some(this.getClass.getClassLoader))
    try {
      val engine = system.actorOf(Node.props(), "engine")
      block(engine)
    } finally {
      system.shutdown()
    }
  }

  "Npm" should {
    "perform an update, retrieve resources, and execute node-gyp (the native compilation tool)" in {
      withEngine {
        engine =>
          implicit val timeout = Timeout(60.seconds)

          // cleanup from any past tests
          FileUtils.deleteDirectory(new File("node_modules"));

          val to = new File(new File("target"), "webjars")
          val cacheFile = new File(to, "extraction-cache")
          val npm = new Npm(engine, NpmLoader.load(to, cacheFile, this.getClass.getClassLoader), verbose = true)
          val pendingResult = npm.update()

          val result = Await.result(pendingResult, timeout.duration)

          val stdErr = new String(result.error.toArray, "UTF-8")
          val stdOut = new String(result.output.toArray, "UTF-8")
          println("=== STDERR ===")
          println(stdErr)
          println()
          println("=== STDOUT ===")
          println(stdOut)

          result.exitValue must_== 0
          stdErr must contain("npm http request GET https://registry.npmjs.org/amdefine")
          stdOut must contain("> node-gyp rebuild")
      }

    }
  }
}
