package com.typesafe.npm

import akka.actor.ActorRef
import scala.concurrent.Future
import com.typesafe.jse.Engine
import scala.collection.immutable
import com.typesafe.jse.Engine.JsExecutionResult
import akka.pattern.ask
import scala.collection.mutable.ListBuffer
import java.io.File
import akka.util.Timeout

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
    (engine ? Engine.ExecuteJs(npmFile, args.to[immutable.Seq], timeout.duration)).mapTo[JsExecutionResult]
  }
}


import org.webjars.FileSystemCache
import org.webjars.WebJarExtractor

object NpmLoader {
  /**
   * Extract the NPM WebJar to disk and return its main entry point.
   * @param to The directory to extract to.
   * @param cacheFile The file to use as a cache of extractions (we don't extract unless we need to).
   * @param classLoader The classloader that should be used to locate the Node related WebJars.
   * @return The main JavaScript entry point into NPM.
   */
  def load(to: File, cacheFile: File, classLoader: ClassLoader): File = {
    val cache = new FileSystemCache(cacheFile)
    val extractor = new WebJarExtractor(cache, classLoader)
    extractor.extractAllNodeModulesTo(to)
    cache.save()
    new File(to, "npm" + File.separator + "lib" + File.separator + "npm.js")
  }
}
