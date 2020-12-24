package com.cleverbase
package domain

import java.util.Date

case class Session(userId: String, sessionId: String, ttl: Date)
