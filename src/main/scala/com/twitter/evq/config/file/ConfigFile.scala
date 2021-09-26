package com.twitter.evq.config.file

import com.twitter.evq.config.Config.Line

object ConfigFile {

  // Static factory for readers
  def getReader(path: String): Reader = {
    new LineAwareConfigFileReader(path)
  }

  trait Reader {

    def hasLine(): Boolean

    def nextLine(): Line

    def close(): Unit
  }

  final case class CouldNotBeReadException(private val message: String = "Config file could not be read",
                                           private val cause: Throwable) extends RuntimeException(message, cause)

}
