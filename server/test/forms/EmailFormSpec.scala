package forms

import play.api.data.{FormError, Mapping}
import specs.UnitSpec

class EmailFormSpec extends UnitSpec {

  val form: EmailForm = new EmailForm {}
  val mapping: Mapping[String] = form.emailMapping

  "The emailForm" should {

    "return a string" when {

      "provided with a valid email" in {
        val result = mapping.bind(Map("" -> "email@example.com"))

        result shouldBe Right("email@example.com")
      }
    }

    "return an error" when {
      "provided with an empty email" in {
        val result = mapping.bind(Map("" -> ""))

        result shouldBe Left(Seq(FormError("", "error.required")))
      }

      "provided with an empty email with spaces" in {
        val result = mapping.bind(Map("" -> " "))

        result shouldBe Left(Seq(FormError("", "error.required")))
      }

      "provided with an email without a domain" in {
        val result = mapping.bind(Map("" -> "email@example"))

        result shouldBe Left(Seq(FormError("", "error.email")))
      }

      "provided with an email without an @" in {
        val result = mapping.bind(Map("" -> "email.com"))

        result shouldBe Left(Seq(FormError("", "error.email")))
      }

      "provided with an email without an identifier" in {
        val result = mapping.bind(Map("" -> "@example.com"))

        result shouldBe Left(Seq(FormError("", "error.email")))
      }
    }
  }
}
