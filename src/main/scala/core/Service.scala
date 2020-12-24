package com.cleverbase
package core

import domain.{Session, Token, User}
import rest.ErrorResponse
import spray.http.StatusCodes

import java.util.Date

trait DaoService {
  def getUsers(): Either[ErrorResponse, List[User]]
  def getTokens(): Either[ErrorResponse, List[Token]]
  def getSessions(): Either[ErrorResponse, List[Session]]
  def insertTokens(tokens: List[Token]): Either[ErrorResponse, Boolean]
  def insertSessions(tokens: List[Session]): Either[ErrorResponse, Boolean]
}

case class Service (implicit service: DaoService) {

  protected def uuid = java.util.UUID.randomUUID.toString

  def printAdminToken() = {

    val tokens = service.getTokens() match {
      case Right(r) => r
    }

    val adminToken = tokens.find(_.userId == "admin").getOrElse(Token("","", null)).token

    println("\n\n --- Admin Token: " + adminToken + " --- \n\n")
  }

  /**
   * If Authenticate return SessionID
   * @param userId
   * @param password
   * @param token
   * @return
   */
  def authenticate(userId: String, password: String, userToken: String): Either[ErrorResponse, String] = {

    val users = service.getUsers() match {
      case Right(r) => r
      case Left(l) => return Left(l)
    }

    val tokens = service.getTokens() match {
      case Right(r) => r
      case Left(l) => return Left(l)
    }

    if(!users.exists(user => user.userId == userId && user.password == password)) {
      return Left(ErrorResponse(StatusCodes.Forbidden, "Authentication error"))
    }

    val hasMateToken = tokens.exists(token => token.userId == userId && token.token == userToken)

    if(!hasMateToken) {

      // Check if has admin Token
      val hasAdminToken = tokens.find(_.userId == "admin").getOrElse(Token("","", null)).token == userToken

      if(!hasAdminToken) {
        return Left(ErrorResponse(StatusCodes.Forbidden, "Authentication error"))
      }
    }

    // Return session id
    val sessionId = uuid
    service.insertSessions(List(Session(userId, sessionId, new Date()))) match {
      case Right(_) => Right("LOGGED!! Session id: " + sessionId)
      case Left(a) => Left(a)
    }
  }

  /**
   * Return a token if granted, needed to mate authentication
   *
   * @param userId
   * @param sessionId
   * @param mateToGrant
   * @return
   */
  def grantMateAuthentication(userId: String, sessionId: String, mateToGrant: String): Either[ErrorResponse, String] = {

    val users = service.getUsers() match {
      case Right(r) => r
      case Left(l) => return Left(l)
    }

    val sessions = service.getSessions() match {
      case Right(r) => r
      case Left(l) => return Left(l)
    }

    if(!sessions.exists(session => session.userId == userId && session.sessionId == sessionId)) {
      return Left(ErrorResponse(StatusCodes.Forbidden, "User " + userId + " not authenticated"))
    }

    if(!users.exists(user => user.userId == mateToGrant && user.mateId == userId)) {
      return Left(ErrorResponse(StatusCodes.Forbidden, "User " + sessionId + " not mate of " + mateToGrant))
    }

    // Return token
    val token = uuid
    service.insertTokens(List(Token(mateToGrant, token, new Date()))) match {
      case Right(_) => Right("Generated token: " + token)
      case Left(a) => Left(a)
    }

    // I would send token by message using mate telephone
  }

  def grantPermissionToSecret(userId: String, sessionId: String, userToGrant: String, secretId: String, permission: String) = ???

  def viewSecret(userId: String, sessionId: String, secretId: String) = ???
}
