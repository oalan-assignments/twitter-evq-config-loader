package com.twitter.evq.config.file

import com.twitter.evq.config.Config.{BlankLine, CommentOnlyLine, GroupLine, PropertyLine}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.twitter.evq.config.file.{LineAwareConfigFileReader => LineAwareReader}

class ConfigFileReaderSpec extends AnyFlatSpec with Matchers {

  "Lines" should "be mapped to their specific types" in {

    LineAwareReader.getTypedLine("; Some comment") shouldBe CommentOnlyLine("; Some comment")

    LineAwareReader.getTypedLine("         ") shouldBe BlankLine()

    LineAwareReader.getTypedLine("[common] ; this denotes the start of a group called common") shouldBe
      GroupLine("common")

    LineAwareReader.getTypedLine("path = /tmp") shouldBe
      PropertyLine("path = /tmp")
  }

  "Reader" should "be able to read line by line from a config file" in {
    val configFilePath = os.pwd/"src"/"test"/"resources"/"sample.conf"
    val reader = ConfigFile.getReader(configFilePath.toString())
    reader.hasLine() shouldBe true
    reader.nextLine() shouldBe CommentOnlyLine("; This is the config file format your code should accept.")
    reader.nextLine() shouldBe BlankLine()
    reader.nextLine() shouldBe GroupLine("common")
    reader.nextLine() shouldBe PropertyLine("basic_size_limit = 26214400")
    reader.close()
  }

  "Reader" should "throw RuntimeException in case no file could be read" in {
    val thrown = intercept[ConfigFile.CouldNotBeReadException] {
      ConfigFile.getReader("foobar")
    }
    thrown.getMessage shouldBe  "Error while accessing config file"
  }
}
