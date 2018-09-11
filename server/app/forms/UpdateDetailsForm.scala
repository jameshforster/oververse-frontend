package forms

import models.UpdateDetailsModel
import play.api.data.Form
import play.api.data.Forms._

object UpdateDetailsForm extends EmailForm {

  val updateDetailsForm = Form(
    mapping(
      "password" -> nonEmptyText,
      "newPassword" -> optional(text),
      "newEmail" -> optional(emailMapping)
    )(UpdateDetailsModel.apply)(UpdateDetailsModel.unapply)
  )
}
