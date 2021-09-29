package com.twitter.evq.config.query

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.twitter.evq.config.common.Config.{Decoder, Group, ListMappings, QueryConfig}

import java.util.concurrent.TimeUnit
import scala.concurrent.Await

class InMemoryConfigGroupCoordinator(group: ActorRef) extends Group {

  implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  override def get[T](key: String)(implicit decoder: Decoder[T]): Option[T] = {
    val future = group ? QueryConfig(key)
    val result = Await.result(future, timeout.duration).asInstanceOf[Option[String]]
    val finalResult = result.flatMap(ValueDecoders.decode[T](_))
    finalResult
  }

  override def listMappings(): Option[Map[String, String]] = {
    val future = group ? ListMappings
    val result = Await.result(future, timeout.duration).asInstanceOf[Option[Map[String,String]]]
    result
  }
}

