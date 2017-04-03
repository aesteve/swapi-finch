package com.github.aesteve.finch.examples.swapi

import java.net.{InetSocketAddress, URL}

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.{ObjectMapper, PropertyNamingStrategy}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.twitter.finagle.Http
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.http.Status.{Ok => OK}
import com.twitter.finagle.client.Transporter
import com.twitter.finagle.http.RequestBuilder
import io.finch._
import com.twitter.util.{Await, Future}
import com.github.aesteve.finch.examples.swapi.PipeOps._
import com.github.aesteve.finch.examples.swapi.util.InetAddress

object Server extends App {

  @JsonIgnoreProperties(ignoreUnknown = true)
  case class SwCharacter(birthYear: String)

  implicit val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)

  val proxy: Option[InetSocketAddress] = System.getenv("https_proxy") match {
    case InetAddress(address) => Some(address)
    case _ => None
  }

  val swApiClient = Http.client
    .configured(Transporter.HttpProxy(proxy, None))
    .newService("swapi.co:80")

  def fetchCharacter(id: Int): Request =
    RequestBuilder()
      .url(s"https://swapi.co/api/people/$id/")
      .buildGet()

  def readBirthDate: Future[Response] => Future[Output[String]] = { f =>
    f map { resp =>
      resp.status match {
        case OK =>
          (
            resp.contentString
            |> mapper.readValue[SwCharacter]
          ).birthYear |> Ok
        case status => InternalServerError(new RuntimeException(s"$status"))
      }
    }
  }

  val api = get("api" :: "people" :: int) { id: Int => (
    id
    |> fetchCharacter
    |> swApiClient
    |> readBirthDate
  )}

  Await.ready(Http.server.serve(":9001", api.toServiceAs[Text.Plain]))
}
