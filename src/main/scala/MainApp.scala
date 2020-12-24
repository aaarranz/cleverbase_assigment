package com.cleverbase
import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.util.Timeout
import dao.PostgresqlConnector
import rest.RestServiceActor
import spray.can.Http

import scala.concurrent.duration.DurationInt


object MainApp extends App {

  implicit val system = ActorSystem("cleverbase-assigment")

  val api = system.actorOf(Props(new RestServiceActor), "restInterface")

  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(10 seconds)


  IO(Http) ! Http.Bind(api, "0.0.0.0", 9090)
}
