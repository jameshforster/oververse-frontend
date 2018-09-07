package models

import play.api.libs.json.{Json, OFormat}

case class UpdateIndividualDetailModel(username: String, password: String, updatedValue: String)

object UpdateIndividualDetailModel {
  implicit val formats: OFormat[UpdateIndividualDetailModel] = Json.format[UpdateIndividualDetailModel]
}
