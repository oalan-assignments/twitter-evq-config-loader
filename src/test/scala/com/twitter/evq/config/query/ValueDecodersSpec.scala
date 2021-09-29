package com.twitter.evq.config.query

import com.twitter.evq.config.query.ValueDecoders
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ValueDecodersSpec extends AnyFlatSpec with Matchers {

  import ValueDecoders._

  "Int value which is encoded as string" should "yield some int value" in {
    ValueDecoders.decode[Int]("1") shouldBe Some(1)
  }

  "Double value which is encoded as string" should "yield some double value" in {
    ValueDecoders.decode[Double]("1.0") shouldBe Some(1.0)
  }

  "Long value which is encoded as string" should "yield some long value" in {
    ValueDecoders.decode[Double]("2147483648") shouldBe Some(2147483648L)
  }

  "Non-integer value which is encoded as string" should "yield None" in {
    ValueDecoders.decode[Int]("foo") shouldBe None
  }


  "Array of String values" should "yield Array[String]" in {
    ValueDecoders.decode[Array[String]]("array,of,values").iterator.sameElements(Array("array", "of", "values"))
  }

  "Boolean value that is encoded in various string forms" should "yield a valid/correct boolean" in {
    ValueDecoders.decode[Boolean]("no") shouldBe Some(false)
    ValueDecoders.decode[Boolean]("0") shouldBe Some(false)
    ValueDecoders.decode[Boolean]("false") shouldBe Some(false)
    ValueDecoders.decode[Boolean]("yes") shouldBe Some(true)
    ValueDecoders.decode[Boolean]("1") shouldBe Some(true)
    ValueDecoders.decode[Boolean]("true") shouldBe Some(true)
    ValueDecoders.decode[Boolean]("most probably") shouldBe None

  }

}
