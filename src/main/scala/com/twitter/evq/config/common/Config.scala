package com.twitter.evq.config.common

import akka.util.Timeout

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

object Config {

  trait Decoder[T] {
    def decode(d: String): Option[T]
  }

  trait Group {
    def get[T](key: String)(implicit decoder: Decoder[T]): Option[T]

    def listMappings(): Option[Map[String, String]]
  }

  trait Repository {
    def get(group: String): Option[Group]
  }

  case class PropertyData(key: String, value: String, overrideVal: Option[String])

  // Messages for actors
  case class ProcessLine(line: String)
  final case class QueryConfig(key: String)
  final object ListMappings

  //TODO: Read from a config file or better consider calculating numbers based on the size of the config(s)
  val DefaultTimeUnit = TimeUnit.SECONDS
  val DefaultTimeout: Timeout = Timeout(5, DefaultTimeUnit)
  val DefaultRetryDelay: FiniteDuration = FiniteDuration(1, DefaultTimeUnit)
  val DefaultRetryAttempts = 5
  val DefaultCacheSize = 1000

  case class ActorTimingParams(timeout: Timeout, retryDelay: FiniteDuration, retryAttempts: Int)
}
