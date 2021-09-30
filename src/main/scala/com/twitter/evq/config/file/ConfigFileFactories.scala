package com.twitter.evq.config.file

import akka.actor.{Actor, ActorRef}
import com.twitter.evq.config.common.InMemoryConfigGroupNode
import com.twitter.evq.config.file.ConfigFile.{Loader, Reader}
import com.twitter.evq.config.query.InMemoryConfigs

import scala.collection.mutable


object ConfigFileFactories {

  // Static factories
  def getReader(path: String): Reader = {
    new LineAwareConfigFileReader(path)
  }

  def getLoader(): Loader = {
    InMemoryConfigLoader
  }

  def getGroupNode(invokedBy: Any, name: String, overrides: List[String]): Actor = {
    invokedBy match {
      case  InMemoryConfigLoader => new InMemoryConfigGroupNode(name, overrides, mutable.Map.empty)
      case _ => throw new RuntimeException("Illegal invocation!")
    }
  }

  def toConfigs(invokedBy: Any, groupToActor: Map[String, ActorRef]) = {
    invokedBy match {
      case InMemoryConfigLoader =>  new InMemoryConfigs(groupToActor)
      case _ => throw new RuntimeException("Illegal invocation!")
    }
  }
}
