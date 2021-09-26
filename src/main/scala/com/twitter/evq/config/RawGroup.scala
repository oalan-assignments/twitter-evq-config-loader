package com.twitter.evq.config

import com.twitter.evq.config.Config.{Decoder, Group}

case class RawGroup(db: Map[String, String]) extends Group {

  def get[T](k: String)(implicit decoder: Decoder[T]): Option[T] = {
    db.get(k).flatMap(x => decoder.decode(x))
  }

}
