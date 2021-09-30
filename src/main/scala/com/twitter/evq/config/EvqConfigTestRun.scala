package com.twitter.evq.config

import akka.actor.ActorSystem
import akka.actor.typed.scaladsl.adapter.ClassicActorSystemOps
import com.twitter.evq.config.file.{ConfigFile, ConfigFileFactories}
import com.twitter.evq.config.common.Config.Group

import scala.concurrent.ExecutionContext

object EvqConfigTestRun extends App {

  import com.twitter.evq.config.query.ValueDecoders._

  val system = ActorSystem("ConfigSystem")

  val filePath: String = (os.pwd / "src" / "test" / "resources" / "sample.conf").toString()
  val config = ConfigFileFactories.getLoader(system).loadConfig(filePath, List("ubuntu", "production"))

  val paid_users_size_limit: Option[Long] = config.get("common").flatMap(_.get[Long]("paid_users_size_limit"))
  println(s"common:paid_users_size_limit: $paid_users_size_limit")

  val name: Option[String] = config.get("ftp").flatMap(_.get[String]("name"))
  println(s"ftp:name: $name")

  val lastname: Option[String] = config.get("ftp").flatMap(_.get[String]("lastname"))
  println(s"ftp:lastname: $lastname")

  val params: Option[Array[String]] = config.get("http").flatMap(_.get[Array[String]]("params"))
  println(s"http:params: $params")

  val enabled: Option[String] = config.get("ftp").flatMap(_.get[String]("enabled"))
  println(s"ftp:enabled: $name")

  val path: Option[String] = config.get("ftp").flatMap(_.get[String]("path"))
  println(s"ftp:path: $path")

  val pathMappings: Option[Group] = config.get("ftp")
  println(s"ftp: ${pathMappings.get.listMappings()}")

  //Remaining should be fetched from cache, so no actor interaction should be seen
  val name2: Option[String] = config.get("ftp").flatMap(_.get[String]("name"))
  println(s"ftp:name: $name2")

  val enabled2: Option[String] = config.get("ftp").flatMap(_.get[String]("enabled"))
  println(s"ftp:enabled: $enabled2")

  system.terminate()

}
