package com.twitter.evq.config.file

import akka.actor.{Actor, ActorRef, ActorSystem}
import com.twitter.evq.config.common.Config.{ActorTimingParams, DefaultCacheSize, DefaultRetryAttempts, DefaultRetryDelay, DefaultTimeout}
import com.twitter.evq.config.common.InMemoryConfigGroupNode
import com.twitter.evq.config.file.ConfigFile.{Loader, Reader}
import com.twitter.evq.config.query.InMemoryConfigs

import scala.collection.mutable


object ConfigFileFactories {

  // Static factories
  def getReader(invokedBy: AnyRef, path: String): Reader = {
    invokedBy match {
      case _: InMemoryConfigLoader => new LineAwareConfigFileReader(path)
      case _ => throw new RuntimeException("Illegal invocation!")
    }
  }

  def getLoader(system: ActorSystem): Loader = {
    new InMemoryConfigLoader(system)
  }

  def getGroupNode(invokedBy: AnyRef, name: String, overrides: List[String]): Actor = {
    invokedBy match {
      case _: InMemoryConfigLoader => new InMemoryConfigGroupNode(name, overrides, mutable.LinkedHashMap.empty)
      case _ => throw new RuntimeException("Illegal invocation!")
    }
  }

  def toConfigs(invokedBy: AnyRef, groupToActor: Map[String, ActorRef], system: ActorSystem): InMemoryConfigs = {
    invokedBy match {
      case _: InMemoryConfigLoader => new InMemoryConfigs(
        groupToActor,
        system,
        ActorTimingParams(DefaultTimeout, DefaultRetryDelay, DefaultRetryAttempts), DefaultCacheSize)
      case _ => throw new RuntimeException("Illegal invocation!")
    }
  }
}
