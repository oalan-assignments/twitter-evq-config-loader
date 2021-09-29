package com.twitter.evq.config.query

import com.twitter.evq.config.common.Config.Decoder
import org.slf4j.{Logger, LoggerFactory}

import scala.util.{Failure, Success, Try}

object ValueDecoders {

  private val logger: Logger = LoggerFactory.getLogger(ValueDecoders.getClass)

  implicit val stringDecoder: Decoder[String] = (value: String) => Some(value)
  implicit val intDecoder: Decoder[Int] = (value: String) => toNum(value, x => x.toInt, "Integer")
  implicit val longDecoder: Decoder[Long] = (value: String) => toNum(value, x => x.toLong, "Long")
  implicit val doubleDecoder: Decoder[Double] = (value: String) => toNum(value, x => x.toDouble, "Double")
  implicit val booleanDecoder: Decoder[Boolean] = (value: String) => {
    value match {
      case "yes" | "true" | "1" => Some(true)
      case "no" | "false" | "0" => Some(false)
      case _ => None
    }
  }
  implicit val stringArrayDecoder: Decoder[Array[String]] = (value: String) => Some(value.split(","))

  private def toNum[T](value: String, funStrToType: String => T, typeStr: String): Option[T] = {
    Try {
      funStrToType(value)
    } match {
      case Success(value) => Some(value)
      case Failure(exception) => exception match {
        case e: NumberFormatException =>
          logger.error(s"Value '$value' is not a valid ${typeStr}")
          None
        case e: Exception => print("An unexpected error occurred while fetching value")
          None
      }
    }
  }

  def decode[T](value: String)(implicit decoder: Decoder[T]): Option[T] = {
    decoder.decode(value)
  }
}
