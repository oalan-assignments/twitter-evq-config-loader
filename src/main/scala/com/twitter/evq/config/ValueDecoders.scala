package com.twitter.evq.config

import com.twitter.evq.config.Config.Decoder

import scala.util.{Failure, Success, Try}

object ValueDecoders {

  implicit val stringDecoder: Decoder[String] = (d: String) => Some(d)
  implicit val intDecoder: Decoder[Int] = (d: String) => Try {
    d.toInt
  } match {
    case Success(value) => Some(value)
    case Failure(exception) => exception match {
      case e: NumberFormatException => {
        print(s"Value $d is not an Integer")
        None
      }
    }
  }

  implicit val stringArrayDecoder: Decoder[Array[String]] = (d: String) => {
    Some(d.split(","))
  }

}
