package com.twitter.evq.config



object EvqConfig {

  def loadConfig(): EvqConfig = {
    new EvqConfig()
  }
}

class EvqConfig() {

}


class Group(m : Map[String, String]) {

}


/** TODO: Implement line parsing logic here so you could easily test */
class FileParser(file: String) {

  def nextLine(): Line = ???

  trait Line {
    def content: String
  }
  case class PropertyLine(content: String) extends Line
  case class EmptyLine(content: String) extends Line
  case class CommentLine(content: String) extends Line
  case class GroupLine(content: String) extends Line
}