package com.twitter.evq.config.file

import akka.actor.{ActorRef, ActorSystem, Props}
import com.twitter.evq.config.common.Config.{ProcessLine, Repository}
import com.twitter.evq.config.common.InMemoryConfigGroupNode
import com.twitter.evq.config.file.ConfigFile.{CommentOnlyLine, GroupLine, Loader, PropertyLine}
import com.twitter.evq.config.query.InMemoryConfigs
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable

protected[file] object InMemoryConfigLoader extends Loader {

  private val logger: Logger = LoggerFactory.getLogger(InMemoryConfigLoader.getClass)
  var currentActor: ActorRef = _

  override def loadConfig(path: String, overrides: List[String]): Repository = {
    val lookup: mutable.Map[String, ActorRef] = mutable.Map.empty
    val system = ActorSystem("ConfigSystem")
    val reader = ConfigFile.getReader(path)
    var currentGroup = ""
    while (reader.hasLine()) {
      reader.nextLine() match {
        case GroupLine(groupName) => {
          currentGroup = groupName
          //TODO: Use factory
          currentActor = system.actorOf(Props(new InMemoryConfigGroupNode(
            currentGroup, overrides, mutable.Map.empty)), currentGroup)
          lookup.put(groupName, currentActor)
        }
        case PropertyLine(content) => {
          currentActor ! ProcessLine(content)
        }
        case CommentOnlyLine(content) => logger.info("Skipping comment line {}", content)
        case _ => logger.info("Skipping empty or irrelevant line")
      }
    }
    reader.close()
    //TODO: Use factory
    new InMemoryConfigs(lookup.toMap)
  }
}

