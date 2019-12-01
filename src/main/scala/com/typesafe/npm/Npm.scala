package com.typesafe.npm

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.jse.Engine
import com.typesafe.jse.Engine.JsExecutionResult
import java.io.File
import scala.collection.immutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.Future

/**
 * A JVM class for performing NPM commands. Requires a JS engine to use.
 */
class Npm(engine: ActorRef, npmFile: File, verbose: Boolean = false) {

  def this(engine: ActorRef, npmFile: File) = this(engine, npmFile, false)

  def update(global: Boolean = false, names: Seq[String] = Nil)
            (implicit timeout: Timeout): Future[JsExecutionResult] = {
    val args = ListBuffer[String]()
    args += "update"
    if (global) args += "-g"
    if (verbose) args += "--verbose"
    args ++= names
    invokeNpm(args)
  }
  
  // TODO: Stream the stdio Source objects once they become available.
  private def invokeNpm(args: ListBuffer[String])
                       (implicit timeout: Timeout): Future[JsExecutionResult] = {
    if (!Await.result((engine ? Engine.IsNode).mapTo[Boolean], timeout.duration)) {
      throw new IllegalStateException("node not found: a Node.js installation is required to run npm.")
    }
    (engine ? Engine.ExecuteJs(npmFile, args.to[immutable.Seq], timeout.duration)).mapTo[JsExecutionResult]
  }

}


import org.webjars.WebJarExtractor

object NpmLoader {
  /**
   * Extract the NPM WebJar to disk and return its main entry point.
   * @param to The directory to extract to.
   * @param classLoader The classloader that should be used to locate the Node related WebJars.
   * @return The main JavaScript entry point into NPM.
   */
  def load(to: File, classLoader: ClassLoader): File = {
    val extractor = new WebJarExtractor(classLoader)
    extractor.extractAllNodeModulesTo(to)
    new File(to, "npm" + File.separator + "lib" + File.separator + "npm.js")
  }
}
