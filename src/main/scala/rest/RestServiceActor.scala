package com.cleverbase
package rest

import akka.actor.Actor
import core.{DaoService, Service}
import dao.PostgresqlConnector
import spray.http.{StatusCode, StatusCodes}
import spray.routing._

case class ErrorResponse(code: StatusCode, message: String)

/**
 * REST Service actor.
 */
class RestServiceActor extends HttpServiceActor
  with RestApi {

  def receive = runRoute(routes)
}

trait RestApi extends HttpService {
  actor: Actor =>

  implicit val postgresqlConnector = PostgresqlConnector
  val serverService = Service()

  println("\n\n Server initialized"+serverService.printAdminToken())

  def routes: Route =

    pathPrefix("health") {
      pathEnd {
        get {
          parameters("userId", "secretId") { (userId, secretId) =>
            complete {
              "Hello " + userId + " from Cleverbase assigment Server. Your " + secretId + " is save with me"
            }
          }
        }
      }
    } ~
      pathPrefix("authenticate") {
        pathEnd {
          parameters("userid","password","token") { (userid, password, token) =>
              requestContext => handleRequest(requestContext)(serverService.authenticate(userid, password, token))
          }
        }
      } ~
      pathPrefix("grantAuthentication") {
        pathEnd {
          parameters("userid","sessionid","mate") { (userid, sessionid, mate) =>
            requestContext => handleRequest(requestContext)(serverService.grantMateAuthentication(userid, sessionid, mate))
          }
        }
      }

  /**
   * Handles an incoming request and create valid response for it.
   *
   * @param ctx         request context
   * @param successCode HTTP Status code for success
   * @param action      action to perform
   */
  protected def handleRequest (ctx: RequestContext, successCode: StatusCode = StatusCodes.OK) (action: => Either[ErrorResponse, String] ) {
    action match {
      case Right(result: String) =>
        ctx.complete(successCode, result)
      case Left(error: ErrorResponse) =>
        ctx.complete(error.code, error.message)
      case _ =>
        ctx.complete(StatusCodes.InternalServerError)
    }
  }
}



