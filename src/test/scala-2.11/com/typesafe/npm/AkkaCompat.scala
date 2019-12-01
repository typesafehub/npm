package com.typesafe.npm

import akka.actor.ActorSystem
import scala.concurrent.Await
import scala.concurrent.duration._

object AkkaCompat {
  def terminate(system: ActorSystem): Unit = {
    Await.ready(system.terminate(), 10.seconds)
  }
}
