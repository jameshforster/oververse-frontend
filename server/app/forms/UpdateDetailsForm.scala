package forms

import models.UpdateDetailsModel
import play.api.data.Form
import play.api.data.Forms._

object UpdateDetailsForm {

  val updateDetailsForm = Form(
    mapping(
      "password" -> nonEmptyText,
      "newPassword" -> optional(text),
      "newEmail" -> optional(email)
    )(UpdateDetailsModel.apply)(UpdateDetailsModel.unapply)
  )
}
