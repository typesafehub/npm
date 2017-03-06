package com.typesafe.jse

import org.specs2.mutable.Specification
import java.io.File
import org.specs2.time.NoTimeConversions
import akka.util.Timeout
import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.npm.{Npm, NpmLoader}

import scala.concurrent.Await
import scala.concurrent.duration._

class NpmSpec extends Specification with NoTimeConversions {

  def withEngine[T](block: ActorRef => T): T = {
    // First, clean up node_modules, if it exists
    def delete(file: File): Unit = {
      if (file.exists) {
        if (file.isDirectory) {
          file.listFiles().foreach(delete)
        }
        file.delete()
      }
    }
    delete(new File("node_modules"))


    val system = ActorSystem("test-system", classLoader = Some(this.getClass.getClassLoader))
    try {
      val engine = system.actorOf(Trireme.props(), "engine")
      block(engine)
    } finally {
      system.shutdown()
    }
  }

  "Npm" should {
    "perform an update and retrieve resources" in {
      withEngine {
        engine =>
          implicit val timeout = Timeout(60.seconds)

          val to = new File(new File("target"), "webjars")
          val cacheFile = new File(to, "extraction-cache")
          val npm = new Npm(engine, NpmLoader.load(to, cacheFile, this.getClass.getClassLoader), verbose = true)
          val pendingResult = npm.update()

          val result = Await.result(pendingResult, timeout.duration)

          result.exitValue must_== 0
          val stdErr = new String(result.error.toArray, "UTF-8")
          val stdOut = new String(result.output.toArray, "UTF-8")
          println("=== STDERR ===")
          println(stdErr)
          println()
          println("=== STDOUT ===")
          println(stdOut)

          stdErr must contain("npm http request GET https://registry.npmjs.org/amdefine")
          stdOut must contain("> node-gyp rebuild")
      }

    }
  }
}
