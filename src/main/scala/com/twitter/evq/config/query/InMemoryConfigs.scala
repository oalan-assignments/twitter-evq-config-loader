package com.twitter.evq.config.query

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.twitter.evq.config.common.Config._

import java.util.concurrent.TimeUnit
import scala.concurrent.Await

class InMemoryConfigs(groupToRepoNodes: Map[String, ActorRef]) extends Repository with Group {

  // Used imperative approach to avoid changing method signatures too much
  var nodeActor: ActorRef = _

  implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  //TODO: Consider using LRU cache

  override def get(group: String): Option[Group] = {
    val node: Option[ActorRef] = groupToRepoNodes.get(group)
    node match {
      case Some(actor) =>
        nodeActor = actor
        Some(this)
      case None => None
    }
  }
  
  //TODO: In methods below we are waiting (blocking) to return the values. However once the config file is very large
  //actors might be still busy with handling the lines streamed to them. In that case timeout may need to change (maybe
  //reading from a config file? or better option would be returning Futures from the methods below
  override def get[T](key: String)(implicit decoder: Decoder[T]): Option[T] = {
    val future = nodeActor ? QueryConfig(key)
    val result = Await.result(future, timeout.duration).asInstanceOf[Option[String]]
    val finalResult = result.flatMap(ValueDecoders.decode[T](_))
    finalResult
  }

  override def listMappings(): Option[Map[String, String]] = {
    val future = nodeActor ? ListMappings
    val result = Await.result(future, timeout.duration).asInstanceOf[Option[Map[String,String]]]
    result
  }

}