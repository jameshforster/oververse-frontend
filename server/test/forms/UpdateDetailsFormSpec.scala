package forms

import models.UpdateDetailsModel
import specs.UnitSpec

class UpdateDetailsFormSpec extends UnitSpec {

  "The updateDetailsForm" should {

    "convert a model to a correct form data format" when {

      "with all optional values" in {
        UpdateDetailsForm.updateDetailsForm.fill(UpdateDetailsModel("password", Some("newPassword"),
          Some("newEmail@example.com"))).data shouldBe Map("password" -> "password", "newPassword" -> "newPassword", "newEmail" -> "newEmail@example.com")
      }

      "without optional values" in {
        UpdateDetailsForm.updateDetailsForm.fill(UpdateDetailsModel("password", None,
          None)).data shouldBe Map("password" -> "password")
      }
    }

    "have no errors" when {

      "provided with valid optional values" in {
        val result = UpdateDetailsForm.updateDetailsForm.bind(Map(
          "password" -> "password",
          "newPassword" -> "newPassword",
          "newEmail" -> "newEmail@example.com"
        ))

        result.hasErrors shouldBe false
        result.value shouldBe Some(UpdateDetailsModel("password", Some("newPassword"), Some("newEmail@example.com")))
      }

      "provided with valid non-optional values" in {
        val result = UpdateDetailsForm.updateDetailsForm.bind(Map(
          "password" -> "password"
        ))

        result.hasErrors shouldBe false
        result.value shouldBe Some(UpdateDetailsModel("password", None, None))
      }
    }

    "have one error" when {

      "provided with an empty password" in {
        val result = UpdateDetailsForm.updateDetailsForm.bind(Map(
          "password" -> ""
        ))

        result.hasErrors shouldBe true
        result.errors.size shouldBe 1
        result.errors.head.key shouldBe "password"
        result.errors.head.message shouldBe "error.required"
      }

      "provided with an invalid email" in {
        val result = UpdateDetailsForm.updateDetailsForm.bind(Map(
          "password" -> "password",
          "newPassword" -> "newPassword",
          "newEmail" -> "newEmail"
        ))

        result.hasErrors shouldBe true
        result.errors.size shouldBe 1
        result.errors.head.key shouldBe "newEmail"
        result.errors.head.message shouldBe "error.email"
      }
    }
  }
}
