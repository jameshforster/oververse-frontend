package forms

import models.UserModel
import play.api.data.Form
import play.api.data.Forms._

object RegisterForm {

  val registerForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText,
      "authLevel" -> optional(number)
        .transform[Int](_.getOrElse(50), value => Some(value)),
      "authToken" -> optional(text)
    )(UserModel.apply)(UserModel.unapply)
  )
}
