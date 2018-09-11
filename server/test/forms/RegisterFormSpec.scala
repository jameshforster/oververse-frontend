package forms

import models.UserModel
import specs.UnitSpec

class RegisterFormSpec extends UnitSpec {

  "The register form" should {

    "convert a model to a correct form data format" when {

      "not provided with optional fields" in {
        RegisterForm.registerForm.fill(UserModel("userName", "email@example.com", "password"))
          .data shouldBe Map("username" -> "userName",
          "email" -> "email@example.com",
          "password" -> "password",
          "authLevel" -> "50"
        )
      }

      "provided with optional fields" in {
        RegisterForm.registerForm.fill(UserModel("userName", "email@example.com", "password", 100, Some("token")))
          .data shouldBe Map("username" -> "userName",
          "email" -> "email@example.com",
          "password" -> "password",
          "authLevel" -> "100",
          "authToken" -> "token"
        )
      }
    }

    "convert form data into a model" when {

      "provided with no optional values" in {
        val result = RegisterForm.registerForm.bind(Map("username" -> "userName",
          "email" -> "email@example.com",
          "password" -> "password"))

        result.hasErrors shouldBe false
        result.value shouldBe Some(UserModel("userName", "email@example.com", "password"))
      }

      "provided with all optional values" in {
        val result = RegisterForm.registerForm.bind(Map("username" -> "userName",
          "email" -> "email@example.com",
          "password" -> "password",
          "authLevel" -> "100",
          "authToken" -> "token"
        ))

        result.hasErrors shouldBe false
        result.value shouldBe Some(UserModel("userName", "email@example.com", "password", 100, Some("token")))
      }
    }

    "return a form with a single error" when {

      "provided with an empty username" in {
        val result = RegisterForm.registerForm.bind(Map("username" -> "",
          "email" -> "email@example.com",
          "password" -> "password"))

        result.hasErrors shouldBe true
        result.errors.size shouldBe 1
        result.errors.head.key shouldBe "username"
        result.errors.head.message shouldBe "error.required"
      }

      "provided with an empty password" in {
        val result = RegisterForm.registerForm.bind(Map("username" -> "userName",
          "email" -> "email@example.com",
          "password" -> ""))

        result.hasErrors shouldBe true
        result.errors.size shouldBe 1
        result.errors.head.key shouldBe "password"
        result.errors.head.message shouldBe "error.required"
      }

      "provided with a non-numeric authLevel" in {
        val result = RegisterForm.registerForm.bind(Map("username" -> "userName",
          "email" -> "email@example.com",
          "password" -> "password",
          "authLevel" -> "text"))

        result.hasErrors shouldBe true
        result.errors.size shouldBe 1
        result.errors.head.key shouldBe "authLevel"
        result.errors.head.message shouldBe "error.number"
      }

      "provided with an invalid email" in {
        val result = RegisterForm.registerForm.bind(Map("username" -> "userName",
          "email" -> "invalidEmail",
          "password" -> "password"))

        result.hasErrors shouldBe true
        result.errors.size shouldBe 1
        result.errors.head.key shouldBe "email"
        result.errors.head.message shouldBe "error.email"
      }
    }
  }
}
