package com.cleverbase
package domain

import java.util.Date

case class Token(userId: String, token: String, ttl: Date)
