package com.twitter.evq.config.query

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import com.twitter.evq.config.common.Config._
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps

class InMemoryConfigs(groupToRepoNodes: Map[String, ActorRef],
                      system: ActorSystem,
                      timingParams: ActorTimingParams,
                      cacheSize: Long) extends Repository with Group {

  // Used imperative approach to avoid changing method signatures too much
  var nodeActor: ActorRef = _

  //TODO: Parameterize timeout and cache max size
  implicit val actorQueryTimeout: Timeout = timingParams.timeout

  //Keeping keys that do not exist too. Maybe bloom filter could be a better option for that purpose?
  val cache: LoadingCache[String, Option[String]] = CacheBuilder
    .newBuilder()
    .maximumSize(cacheSize)
    .build(new Loader())

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
    val result = cache.get(key)
    val finalResult = result.flatMap(ValueDecoders.decode[T](_))
    finalResult
  }

  override def listMappings(): Option[Map[String, String]] = {
    val future = nodeActor ? ListMappings
    val result = Await.result(getRetried(future), actorQueryTimeout.duration).asInstanceOf[Option[Map[String, String]]]
    result
  }

  private class Loader extends CacheLoader[String, Option[String]] {
    val logger: Logger = LoggerFactory.getLogger(this.getClass)

    override def load(key: String): Option[String] = {
      logger.info("Key {} is not in cache fetching it", key)
      val future = nodeActor ? QueryConfig(key)
      val result = Await.result(getRetried(future), actorQueryTimeout.duration).asInstanceOf[Option[String]]
      result
    }
  }

  private def getRetried(future: Future[Any]) = {
    implicit val scheduler: akka.actor.Scheduler = system.scheduler
    implicit val ec: ExecutionContext = system.dispatcher
    akka.pattern.retry(() => future, attempts = timingParams.retryAttempts, timingParams.retryDelay)

  }

}