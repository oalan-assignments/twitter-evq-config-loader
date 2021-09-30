package com.twitter.evq.config.file

import com.twitter.evq.config.common.Config.Repository

object ConfigFile {

  trait Loader {
    def loadConfig(path: String, overrides: List[String]): Repository
  }

  trait Reader {

    def hasLine(): Boolean

    def nextLine(): Line

    def close(): Unit
  }

  trait Line {
    def content: String
  }


  case class PropertyLine(content: String) extends Line

  case class CommentOnlyLine(content: String) extends Line

  case class GroupLine(content: String) extends Line

  case class BlankLine() extends Line {
    def content = ""
  }

  final case class CouldNotBeReadException(private val message: String = "Config file could not be read",
                                           private val cause: Throwable) extends RuntimeException(message, cause)

}
