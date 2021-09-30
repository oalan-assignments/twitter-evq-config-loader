package com.twitter.evq.config.common

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.twitter.evq.config.common.Config.{PropertyData, ListMappings, ProcessLine, QueryConfig}
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.util.concurrent.TimeUnit
import scala.collection.mutable

class InMemoryConfigGroupNodeSpec extends AnyFlatSpec with Matchers {

  "Processing a line" should "yield an entry in given lookup" in {
    val currentData: mutable.Map[String, PropertyData] = mutable.Map.empty
    InMemoryConfigGroupNode.processLine("path = /tmp/", currentData, "test", List("override"))
    currentData.get("path") shouldBe Option(PropertyData("path", "/tmp/", None))
  }

  "While processing a line, if the line has an override that is listed in given overrides, then it" should "override" in {
    val currentData: mutable.Map[String, PropertyData] = mutable.Map.newBuilder.result()
    currentData += ("path" -> PropertyData("path", "/srv/var/tmp", Some("production")))
    InMemoryConfigGroupNode.processLine("path<ubuntu> = /etc/var/uploads",
      currentData, "test", List("ubuntu", "production"))
    currentData.get("path") shouldBe Option(PropertyData("path", "/etc/var/uploads", Some("ubuntu")))
  }

  "While processing a line, if the line has an override that is NOT listed in given overrides, then it" should "NOT override" in {
    val currentData: mutable.Map[String, PropertyData] = mutable.Map.newBuilder.result()
    val extract = PropertyData("path", "/srv/var/tmp", Some("production"))
    currentData += ("path" -> extract)
    InMemoryConfigGroupNode.processLine("path<ubuntu> = /etc/var/uploads",
      currentData, "test", List("staging", "production"))
    currentData.get("path") shouldBe Option(extract)
  }

  "Interactions with config node actor" should "work as expected" in {
    import akka.testkit.TestActorRef
    implicit lazy val system: ActorSystem = ActorSystem()
    implicit val timeout = Timeout(5, TimeUnit.SECONDS)
    val actorRef = TestActorRef(new InMemoryConfigGroupNode("test",
      List(""), mutable.Map.empty))
    actorRef ! ProcessLine("path = /tmp/")
    val queryFuture = actorRef ? QueryConfig("path")
    queryFuture.futureValue shouldBe Some("/tmp/")
    val mappingsFuture = actorRef ? ListMappings
    mappingsFuture.futureValue shouldBe Some(Map("path" -> "/tmp/"))
  }

}
