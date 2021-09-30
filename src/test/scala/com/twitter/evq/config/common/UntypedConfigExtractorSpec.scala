package com.twitter.evq.config.common

import com.twitter.evq.config.common.Config.PropertyData
import com.twitter.evq.config.file.ConfigFile.PropertyLine
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UntypedConfigExtractorSpec extends AnyFlatSpec with Matchers {

  "Basic config line" should "be extracted correctly" in {
    val line = PropertyLine("basic_size_limit = 26214400")
    UntypedConfigExtractor.extractConfigInfo(line.content) shouldBe Some(PropertyData("basic_size_limit", "26214400", None))
  }

  "Config line with override" should "be extracted correctly" in {
    val line = PropertyLine("path<staging> = /srv/uploads")
    UntypedConfigExtractor.extractConfigInfo(line.content) shouldBe Some(PropertyData("path", "/srv/uploads", Some("staging")))
  }

  "Malformed config line" should "be handled properly" in {
    val line = PropertyLine("path<staging>/srv/uploads")
    UntypedConfigExtractor.extractConfigInfo(line.content) shouldBe None
  }
}
