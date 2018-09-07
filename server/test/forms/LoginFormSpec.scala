package forms

import models.LoginModel
import specs.UnitSpec

class LoginFormSpec extends UnitSpec {

  "The loginForm" should {

    "convert a model to a correct form data format" in {
      LoginForm.loginForm.fill(LoginModel("testName", "testPassword")).data shouldBe Map("username" -> "testName", "password" -> "testPassword")
    }

    "have no errors" when {

      "both fields have non-empty text" in {
        val result = LoginForm.loginForm.bind(Map("username" -> "name", "password" -> "testPassword"))

        result.hasErrors shouldBe false
        result.value shouldBe Some(LoginModel("name", "testPassword"))
      }
    }

    "have one error" when {

      "an empty username is provided" in {
        val result = LoginForm.loginForm.bind(Map("username" -> "", "password" -> "testPassword"))

        result.hasErrors shouldBe true
        result.errors.size shouldBe 1
        result.errors.head.key shouldBe "username"
        result.errors.head.message shouldBe "error.required"
      }

      "no username is provided" in {
        val result = LoginForm.loginForm.bind(Map("password" -> "testPassword"))

        result.hasErrors shouldBe true
        result.errors.size shouldBe 1
        result.errors.head.key shouldBe "username"
        result.errors.head.message shouldBe "error.required"
      }

      "an empty password is provided" in {
        val result = LoginForm.loginForm.bind(Map("username" -> "name", "password" -> ""))

        result.hasErrors shouldBe true
        result.errors.size shouldBe 1
        result.errors.head.key shouldBe "password"
        result.errors.head.message shouldBe "error.required"
      }

      "no password is provided" in {
        val result = LoginForm.loginForm.bind(Map("username" -> "name"))

        result.hasErrors shouldBe true
        result.errors.size shouldBe 1
        result.errors.head.key shouldBe "password"
        result.errors.head.message shouldBe "error.required"
      }
    }

    "have multiple errors" when {

      "both fields have empty values" in {
        val result = LoginForm.loginForm.bind(Map("username" -> "", "password" -> ""))

        result.hasErrors shouldBe true
        result.errors.size shouldBe 2
      }

      "both fields are missing" in {
        val result = LoginForm.loginForm.bind(Map("incorrect" -> "value"))

        result.hasErrors shouldBe true
        result.errors.size shouldBe 2
      }
    }
  }
}
