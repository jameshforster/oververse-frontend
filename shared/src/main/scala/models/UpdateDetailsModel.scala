package models

case class UpdateDetailsModel(password: String, newPassword: Option[String], newEmail: Option[String])
