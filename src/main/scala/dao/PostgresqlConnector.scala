package com.cleverbase
package dao

import core.DaoService
import domain.{Token, User, Session}
import rest.ErrorResponse
import spray.http.StatusCodes
import slick.jdbc.PostgresProfile.api._

import java.sql.Date
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt

object PostgresqlConnector extends DaoService {

  class PostgresUsers(tag: Tag) extends Table[(String, String, String, Long)](tag, "users") {
    def userid = column[String]("userid")
    def password = column[String]("password")
    def mateid = column[String]("mateid")
    def telephone = column[Long]("telephone")
    def * = (userid, password, mateid, telephone)
  }

  class PostgresSecrets(tag: Tag) extends Table[(String, String, String)](tag, "secrets") {
    def secretid = column[String]("secretid")
    def owner = column[String]("owner")
    def secret = column[String]("secret")
    def * = (secretid, owner, secret)
  }

  class PostgresSecretsPerms(tag: Tag) extends Table[(String, String)](tag, "secret_perms") {
    def secretid = column[String]("secretid")
    def userid = column[String]("userid")
    def * = (secretid, userid)
  }

  class PostgresSession(tag: Tag) extends Table[(String, String, Option[Date])](tag, "sessions") {
    def userid = column[String]("userid")
    def sessionid = column[String]("sessionid")
    def ttl = column[Option[Date]]("ttl")
    def * = (userid, sessionid, ttl)
  }

  class PostgresToken(tag: Tag) extends Table[(String, String, Option[Date])](tag, "tokens") {
    def userid = column[String]("userid")
    def token = column[String]("token")
    def ttl = column[Option[Date]]("ttl")
    def * = (userid, token, ttl)
  }

  val connectionUrl = "jdbc:postgresql://localhost:5432/postgres?user=postgres"

  def getUsers(): Either[ErrorResponse, List[User]] = {

    val db = Database.forURL(connectionUrl, driver = "org.postgresql.Driver")
    try {

      val tableQuery = TableQuery[PostgresUsers]

      val completedFutureResult = Await.result(db.run(tableQuery.result), 5 seconds)
      Right(completedFutureResult.map(row => User(row._1, row._2, row._3, row._4)).toList)

    } catch {

      case e: Exception =>
        Left(ErrorResponse(StatusCodes.InternalServerError, "Error getting Users - " + e.getMessage))
      case _: Throwable =>
        Left(ErrorResponse(StatusCodes.InternalServerError, "We have some kind of throwable when consulting database"))
    }

    finally db.close
  }

  def getTokens(): Either[ErrorResponse, List[Token]] = {

    val db = Database.forURL(connectionUrl, driver = "org.postgresql.Driver")
    try {

      val tableQuery = TableQuery[PostgresToken]

      val completedFutureResult = Await.result(db.run(tableQuery.result), 5 seconds)
      Right(completedFutureResult.map(row => Token(row._1, row._2, row._3.getOrElse(null))).toList)

    } catch {

      case e: Exception =>
        Left(ErrorResponse(StatusCodes.InternalServerError, "Error getting Tokens - " + e.getMessage))
      case _: Throwable =>
        Left(ErrorResponse(StatusCodes.InternalServerError, "We have some kind of throwable when consulting database"))
    }

    finally db.close
  }

  def getSessions(): Either[ErrorResponse, List[Session]] = {

    val db = Database.forURL(connectionUrl, driver = "org.postgresql.Driver")
    try {

      val tableQuery = TableQuery[PostgresSession]

      val completedFutureResult = Await.result(db.run(tableQuery.result), 5 seconds)
      Right(completedFutureResult.map(row => Session(row._1, row._2, row._3.getOrElse(null))).toList)

    } catch {

      case e: Exception =>
        Left(ErrorResponse(StatusCodes.InternalServerError, "Error getting Sessions - " + e.getMessage))
      case _: Throwable =>
        Left(ErrorResponse(StatusCodes.InternalServerError, "We have some kind of throwable when consulting database"))
    }

    finally db.close
  }

  def insertTokens(tokens: List[Token]): Either[ErrorResponse, Boolean] = {

    val db = Database.forURL(connectionUrl, driver = "org.postgresql.Driver")
    try {

      val tableQuery = TableQuery[PostgresToken]

      val insert = DBIO.seq(
        // Insert some tokens (using JDBC's batch insert feature, if supported by the DB)
        tableQuery ++= tokens.map(token => (token.userId, token.token, Some(new java.sql.Date(token.ttl.getTime())) ))
      )

      val completedFutureResult = Await.result(db.run(insert), 120 seconds)
      Right(true)

    } catch {

      case e: Exception =>
        Left(ErrorResponse(StatusCodes.InternalServerError, "Error inserting Tokens - " + e.getMessage))
      case _: Throwable =>
        Left(ErrorResponse(StatusCodes.InternalServerError, "We have some kind of throwable when consulting database"))
    }

    finally db.close
  }

  def insertSessions(userSessions: List[Session]): Either[ErrorResponse, Boolean] = {

    val db = Database.forURL(connectionUrl, driver = "org.postgresql.Driver")
    try {

      val tableQuery = TableQuery[PostgresSession]

      val insert = DBIO.seq(
        // Insert some tokens (using JDBC's batch insert feature, if supported by the DB)
        tableQuery ++= userSessions.map(userSession => (userSession.userId, userSession.sessionId, Some(new java.sql.Date(userSession.ttl.getTime())) ))
      )

      val completedFutureResult = Await.result(db.run(insert), 120 seconds)
      Right(true)

    } catch {

      case e: Exception =>
        Left(ErrorResponse(StatusCodes.InternalServerError, "Error inserting Sessions - " + e.getMessage))
      case _: Throwable =>
        Left(ErrorResponse(StatusCodes.InternalServerError, "We have some kind of throwable when consulting database"))
    }

    finally db.close
  }
}
