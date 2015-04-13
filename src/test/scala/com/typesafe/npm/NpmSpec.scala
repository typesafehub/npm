package com.typesafe.jse

import org.specs2.mutable.Specification
import java.io.File
import org.specs2.time.NoTimeConversions
import akka.util.Timeout
import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.npm.{NpmLoader, Npm}
import scala.concurrent.Await
import scala.concurrent.duration._

class NpmSpec extends Specification with NoTimeConversions {

  def withEngine[T](block: ActorRef => T): T = {
    val system = ActorSystem("test-system")
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
          val npm = new Npm(engine, NpmLoader.load(to, cacheFile, this.getClass.getClassLoader))
          val pendingResult = npm.update()

          val result = Await.result(pendingResult, timeout.duration)

          result.exitValue must_== 0
          new String(result.error.toArray, "UTF-8") must contain("npm http GET https://registry.npmjs.org/amdefine/latest")
      }

    }
  }
}
