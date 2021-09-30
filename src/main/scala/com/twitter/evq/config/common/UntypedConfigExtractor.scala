package com.twitter.evq.config.common

import com.twitter.evq.config.common.Config.PropertyData
import org.slf4j.{Logger, LoggerFactory}

import scala.util.{Failure, Success, Try}

object UntypedConfigExtractor {

  private val logger: Logger = LoggerFactory.getLogger(UntypedConfigExtractor.getClass)

  def extractConfigInfo(line: String): Option[PropertyData] = {
    val result = Try {
      val Array(keyFragment, valueFragment) = line.split("=")
      (keyFragment, valueFragment)
    } match {
      case Success(result) => Some(result)
      case Failure(exception) =>
        logger.error(s"Config line '$line' is invalid. Reason: ${exception.getClass}")
        None
    }
    result match {
      case Some((keyFragment, valueFragment)) =>
        val splittedKeyFragment = keyFragment.split("<")
        val key = splittedKeyFragment(0).trim
        val overrideVal = if (splittedKeyFragment.size == 2)
          Some(splittedKeyFragment(1).replace(">", "").trim) else None
        val value = valueFragment.split(";")(0).trim
        Some(PropertyData(key, value, overrideVal))
      case None => None
    }
  }
}
