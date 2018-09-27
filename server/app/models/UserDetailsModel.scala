package models

import play.api.libs.json.{Json, OFormat}

case class UserDetailsModel(username: String, email: String, authLevel: Int) {
  def levelToString: String = {
    authLevel match {
      case x if x < 0 => "Banned"
      case x if 0 until UserDetailsModel.verified contains x => "Unverified"
      case x if (UserDetailsModel.verified until UserDetailsModel.moderator) contains x => "Verified"
      case x if (UserDetailsModel.moderator until UserDetailsModel.admin) contains x => "Moderator"
      case x if (UserDetailsModel.admin until UserDetailsModel.owner) contains x => "Administrator"
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
