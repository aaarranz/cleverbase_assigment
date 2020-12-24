package com.cleverbase
package domain

case class Secret(owner: String, secret: String, permissions: List[String])
