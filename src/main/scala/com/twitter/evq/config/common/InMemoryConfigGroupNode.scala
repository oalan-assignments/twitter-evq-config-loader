package com.twitter.evq.config.common

import akka.actor.Actor
import com.twitter.evq.config.common.Config.{PropertyData, ListMappings, ProcessLine, QueryConfig}
import com.twitter.evq.config.common.InMemoryConfigGroupNode.processLine
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable

object InMemoryConfigGroupNode {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private[common] def processLine(line: String,
                                  currentData: mutable.Map[String, PropertyData],
                                  groupName: String,
                                  overrides: List[String]) = {
    val newConfig = UntypedConfigExtractor.extractConfigInfo(line)
    newConfig match {
      case Some(newConfig) =>
        val oldVersion = currentData.get(newConfig.key)
        oldVersion match {
          case Some(oldVersion) =>
            resolveOverrides(newConfig, oldVersion, currentData, overrides)
          case None => currentData.put(newConfig.key, newConfig)
        }
        logger.info("Node {} stored {}:{}", groupName, newConfig.key, newConfig.value)
      case None => logger.error("Could not extract line {}", line)
    }
  }

  private[common] def resolveOverrides(newConfig: PropertyData,
                                       oldVersion: PropertyData,
                                       currentData: mutable.Map[String, PropertyData],
                                       overrides: List[String]) = {
    newConfig.overrideVal.foreach { overrideVal =>
      if (overrides.contains(overrideVal)) {
        currentData.put(newConfig.key, newConfig)
        logger.info("Replacing {} with {} due to override", oldVersion, newConfig)
      }
    }
  }
}

class InMemoryConfigGroupNode(groupName: String,
                              overrides: List[String],
                              data: mutable.Map[String, PropertyData]) extends Actor {


  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def receive: Receive = {
    case QueryConfig(key) =>
      logger.info("[{}] Node got query for key {}", groupName, key)
      sender ! data.get(key).map(_.value)
    case ProcessLine(line) =>
      processLine(line, data, groupName, overrides)
    case ListMappings =>
      val reply: Map[String, String] = data.toMap.map { case (k, e) => (k, e.value) }
      sender ! Some(reply)
    case _ => logger.error("An unknown message received")
  }


}
