package models.exceptions

case class UpstreamUniverseException(status: Int, message: String) extends Exception(message)
