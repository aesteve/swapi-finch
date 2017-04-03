package com.github.aesteve.finch.examples.swapi.util

import java.net.{InetSocketAddress, URL}


object InetAddress {
  def unapply(str: String): Option[InetSocketAddress] =
    str match {
      case null => None
      case address =>
        try {
          val url = new URL(address)
          Some(new InetSocketAddress(url.getHost, url.getPort))
        } catch {
          case e: Exception =>
            val splitted = address.split(":")
            Some(new InetSocketAddress(splitted(0), 443))
        }
    }
}
