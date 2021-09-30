package com.twitter.evq.config.common

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
}
