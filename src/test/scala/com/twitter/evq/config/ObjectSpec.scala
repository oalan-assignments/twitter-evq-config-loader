package com.twitter.evq.config

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ObjectSpec extends AnyFlatSpec with Matchers {

  "Operation" should "work" in {
    import ValueDecoders._
    val dbMap = Map("one" -> "1", "comment" -> "Yes", "values" -> "v1,v2,v3")
    val db = RawGroup(dbMap)
    db.get[Int]("one") shouldBe Some(1)
    db.get[String]("one") shouldBe Some("1")
    db.get[Int]("comment") shouldBe None
    db.get[Int]("something") shouldBe None
    db.get[Array[String]]("values").get.sameElements(Array("v1", "v2", "v3"))
    println(db)

    val m = Map("one" -> 1, "two" -> "two")

    println(m.get("one").get)
    println(m.get("two").get)
  }

}
