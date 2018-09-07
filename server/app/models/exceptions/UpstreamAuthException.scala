package models.exceptions

case class UpstreamAuthException(status: Int, message: String) extends Exception(message)
