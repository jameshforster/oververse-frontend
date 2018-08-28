package forms

import models.LoginModel
import play.api.data.Form
import play.api.data.Forms._

object LoginForm {

  val loginForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
    )(LoginModel.apply)(LoginModel.unapply)
  )
}
