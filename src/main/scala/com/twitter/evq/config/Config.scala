package com.twitter.evq.config

object Config {

  trait Decoder[T] {
    def decode(d: String): Option[T]
  }

  trait Group {
    def get[T](key: String)(implicit decoder: Decoder[T]): Option[T]
  }

  trait Repository {
    def get(group: String): Group
  }

  trait Loader {
    def loadConfig(path: String, overrides: List[String]): Repository
  }

  trait Line {
    def content: String
  }

  case class PropertyLine(content: String) extends Line

  case class BlankLine() extends Line {
    def content = ""
  }

  case class CommentOnlyLine(content: String) extends Line

  case class GroupLine(content: String) extends Line

}
