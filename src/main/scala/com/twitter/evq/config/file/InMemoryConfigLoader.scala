package com.twitter.evq.config.file

import akka.actor.{ActorRef, ActorSystem, Props}
import com.twitter.evq.config.common.Config.{ProcessLine, Repository}
import com.twitter.evq.config.file.ConfigFile.{CommentOnlyLine, GroupLine, Loader, PropertyLine}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable

protected[file] class InMemoryConfigLoader(system: ActorSystem) extends Loader {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)
  var currentActor: ActorRef = _

  override def loadConfig(path: String, overrides: List[String]): Repository = {
    val lookup: mutable.Map[String, ActorRef] = mutable.Map.empty
    val reader = ConfigFileFactories.getReader(this, path)
    var currentGroup = ""
    //TODO: Consider creating actors per given threshold of lines (e.g 1000) to balance actors workload.
    // Then the return object (Repository) would hold map of group name to List of actor references.
    // This could be done with different Loader -> Configs implementation using factory methods...
    while (reader.hasLine) {
      reader.nextLine() match {
        case GroupLine(groupName) =>
          currentGroup = groupName
          currentActor = system.actorOf(Props(
            ConfigFileFactories.getGroupNode(this, currentGroup, overrides)))
          lookup.put(groupName, currentActor)
        case PropertyLine(content) =>
          currentActor ! ProcessLine(content)
        case CommentOnlyLine(content) => logger.info("Skipping comment line {}", content)
        case _ => logger.info("Skipping empty or irrelevant line")
      }
    }
    reader.close()
    ConfigFileFactories.toConfigs(this, lookup.toMap, system)

  }
}

