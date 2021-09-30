package com.twitter.evq.config.file

import akka.actor.ActorSystem
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class InMemoryConfigLoaderSpec extends AnyFlatSpec with Matchers {

  import com.twitter.evq.config.query.ValueDecoders._

  "Config loader" should "spawn actors per group and return Configs object" in {
    val system = ActorSystem("ConfigSystem")
    val configFilePath = os.pwd/"src"/"test"/"resources"/"sample-minimal.conf"
    val configs = new InMemoryConfigLoader(system).loadConfig(configFilePath.toString(), List())
    configs.get("common").flatMap(_.get[Int]("basic_size_limit")) shouldBe Some(26214400)
    configs.get("ftp").flatMap(_.get[String]("name")) shouldBe Some("\"hello there, ftp uploading\"")
    configs.get("something_else") shouldBe None
    system.terminate()
  }

}
