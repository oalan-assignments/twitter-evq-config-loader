package com.twitter.evq.config.file

import com.twitter.evq.config.file.ConfigFile.{BlankLine, CommentOnlyLine, GroupLine, Line, PropertyLine}
import com.twitter.evq.config.file.LineAwareConfigFileReader.getTypedLine

import scala.io.BufferedSource
import scala.util.{Failure, Success, Try}

protected[file] class LineAwareConfigFileReader(path: String) extends ConfigFile.Reader {

  val source: BufferedSource = Try {
    scala.io.Source.fromFile(path)
  } match {
    case Success(source) => source
    case Failure(exception) => throw ConfigFile.CouldNotBeReadException("Error while accessing config file", exception)
  }
  val iterator: Iterator[String] = source.getLines()

  def hasLine(): Boolean = iterator.hasNext

  def nextLine(): Line = {
    val line: String = iterator.next()
    getTypedLine(line)
  }

  def close(): Unit = source.close()
}

protected[file] object LineAwareConfigFileReader {

  def getTypedLine(line: String): Line = {
    if (line.isBlank) {
      BlankLine()
    }
    else {
      val trimmedLine: String = line.trim
      trimmedLine.charAt(0) match {
        case ';' => CommentOnlyLine(trimmedLine)
        case '[' => GroupLine(trimmedLine.substring(1, trimmedLine.indexOf(']')))
        case _ => PropertyLine(trimmedLine)
      }
    }
  }
}
