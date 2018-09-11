package forms

import play.api.data.Forms.of
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.data.format.Formats._

trait EmailForm {

  private val emailConstraint: Constraint[String] = Constraint[String]("constaint.customEmail") { email =>
    val regex = "[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}".r

    if (email.isEmpty) Invalid(ValidationError("error.required"))
    else if (email.trim.isEmpty) Invalid(ValidationError("error.required"))
    else regex.findFirstMatchIn(email.toUpperCase).map(_ => Valid).getOrElse(Invalid(ValidationError("error.email")))
  }

  val emailMapping: Mapping[String] = of[String] verifying emailConstraint
}
