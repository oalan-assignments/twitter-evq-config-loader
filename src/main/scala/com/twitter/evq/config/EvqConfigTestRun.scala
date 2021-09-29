package com.twitter.evq.config

import com.twitter.evq.config.file.ConfigFile
import com.twitter.evq.config.common.Config.Group

object EvqConfigTestRun extends App {

  import com.twitter.evq.config.query.ValueDecoders._

  val filePath: String = (os.pwd / "src" / "test" / "resources" / "sample.conf").toString()
  val config = ConfigFile.getLoader().loadConfig(filePath, List("ubuntu", "production"))

  val paid_users_size_limit: Option[Long] = config.get("common").flatMap(_.get[Long]("paid_users_size_limit"))
  println(s"common:paid_users_size_limit: ${paid_users_size_limit}")

  val name: Option[String] = config.get("ftp").flatMap(_.get[String]("name"))
  println(s"ftp:name: ${name}")

  val lastname: Option[String] = config.get("ftp").flatMap(_.get[String]("lastname"))
  println(s"ftp:lastname: ${lastname}")

  val params: Option[Array[String]] = config.get("http").flatMap(_.get[Array[String]]("params"))
  println(s"http:params: ${params}")

  val enabled: Option[String] = config.get("ftp").flatMap(_.get[String]("enabled"))
  println(s"ftp:enabled: ${name}")

  val path: Option[String] = config.get("ftp").flatMap(_.get[String]("path"))
  println(s"ftp:path: ${path}")

  val pathMappings: Option[Group] = config.get("ftp")
  println(s"ftp: ${pathMappings.get.listMappings()}")

}
