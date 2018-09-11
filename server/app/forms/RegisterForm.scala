package forms

import models.UserModel
import play.api.data.Form
import play.api.data.Forms._

object RegisterForm extends EmailForm {

  val registerForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "email" -> emailMapping,
      "password" -> nonEmptyText,
      "authLevel" -> optional(number)
        .transform[Int](_.getOrElse(50), value => Some(value)),
      "authToken" -> optional(text)
    )(UserModel.apply)(UserModel.unapply)
  )
}
