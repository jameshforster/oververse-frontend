package models

import play.api.libs.json.{Json, OFormat}

case class UserDetailsModel(username: String, email: String, authLevel: Int) {
  def levelToString: String = {
    authLevel match {
      case x if (0 to 99) contains x => "Unverified"
      case x if (100 to 199) contains x => "Verified"
      case x if (200 to 499) contains x => "Moderator"
      case x if (500 to 999) contains x => "Administrator"
      case _ => "Owner"
    }
  }
}

object UserDetailsModel {
  implicit val formats: OFormat[UserDetailsModel] = Json.format[UserDetailsModel]

  val unverified = 50
  val verified = 100
  val moderator = 200
  val admin = 500
  val owner = 1000
}
