package services

import connectors.AuthConnector
import models._
import models.exceptions.UpstreamAuthException
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSResponse
import specs.UnitSpec
import play.api.mvc.Results._
import play.api.http.Status._

import scala.concurrent.Future

class AuthServiceSpec extends UnitSpec {

  def setupService(success: Boolean, status: Int, json: JsValue = Json.toJson(""), message: String = ""): AuthService = {
    val mockConnector = mock[AuthConnector]
    val mockResponse = mock[WSResponse]
    val response = if (success) Future.successful(mockResponse)
    else Future.failed(new Exception(message))

    when(mockResponse.status)
        .thenReturn(status)

    when(mockResponse.body)
        .thenReturn(message)

    when(mockResponse.json)
        .thenReturn(json)

    when(mockConnector.authorise(ArgumentMatchers.any[Int], ArgumentMatchers.any[String]))
      .thenReturn(response)

    when(mockConnector.login(ArgumentMatchers.any[LoginModel]))
      .thenReturn(response)

    when(mockConnector.register(ArgumentMatchers.any[UserModel]))
      .thenReturn(response)

    when(mockConnector.updateEmail(ArgumentMatchers.any[UpdateIndividualDetailModel]))
      .thenReturn(response)

    when(mockConnector.updatePassword(ArgumentMatchers.any[UpdateIndividualDetailModel]))
      .thenReturn(response)

    new AuthService(mockConnector)
  }

  val userDetails = UserDetailsModel("name", "email", 50)

  "Calling .authorise" should {

    "return the user details" when {

      "provided with a valid token and a 200 response" in {
        val result = setupService(true, 200, Json.toJson(userDetails)).authorise(50)(fakeRequestWithToken)

        await(result) shouldBe Some(userDetails)
      }
    }

    "return no user details" when {

      "provided with a valid token and a 401 response" in {
        val result = setupService(true, 401, Json.toJson(userDetails)).authorise(50)(fakeRequestWithToken)

        await(result) shouldBe None
      }

      "provided with no token" in {
        val result = setupService(true, 200, Json.toJson(userDetails)).authorise(50)(fakeRequest)

        await(result) shouldBe None
      }
    }

    "return an exception" when {

      "provided with a valid token and non-matching response" in {
        val result = setupService(true, 500, Json.toJson(userDetails), "error message").authorise(50)(fakeRequestWithToken)

        the[UpstreamAuthException] thrownBy await(result) shouldBe UpstreamAuthException(500, "error message")
      }

      "provided with a valid token and a generic exception" in {
        val result = setupService(false, 200, Json.toJson(userDetails), "generic error message").authorise(50)(fakeRequestWithToken)

        the[Exception] thrownBy await(result) should have message "generic error message"
      }
    }
  }

  "Calling .authoriseOrLogin" should {

    "execute the authorised action" when {

      "user details are returned" in {
        val result = setupService(true, 200, Json.toJson(userDetails)).authoriseOrLogin(50) {
          model => Future.successful(Ok(Json.toJson(model)))
        }(fakeRequestWithToken)

        statusOf(result) shouldBe OK
        bodyOf(result) shouldBe Json.toJson(userDetails).toString()
      }
    }

    "redirect to the login page" when {

      "no user details are returned" in {
        val result = setupService(true, 401, Json.toJson(userDetails)).authoriseOrLogin(50) {
          model => Future.successful(Ok(Json.toJson(model)))
        }(fakeRequestWithToken)

        statusOf(result) shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(controllers.routes.LoginController.getLogin(fakeRequestWithToken.path).url)
      }
    }

    "return an exception" when {

      "an upstream auth exception occurs" in {
        val result = setupService(true, 500, Json.toJson(userDetails), "error message").authoriseOrLogin(50) {
          model => Future.successful(Ok(Json.toJson(model)))
        }(fakeRequestWithToken)

        the[UpstreamAuthException] thrownBy await(result) shouldBe UpstreamAuthException(500, "error message")
      }

      "a generic exception occurs" in {
        val result = setupService(false, 200, Json.toJson(userDetails), "generic error message").authoriseOrLogin(50) {
          model => Future.successful(Ok(Json.toJson(model)))
        }(fakeRequestWithToken)

        the[Exception] thrownBy await(result) should have message "generic error message"
      }
    }
  }

  "Calling .getUserDetails" should {

    "execute the requested action" when {

      "user details are returned" in {
        val result = setupService(true, 200, Json.toJson(userDetails)).getUserDetails(50) {
          model => Future.successful(Ok(Json.toJson(model)))
        }(fakeRequestWithToken)

        statusOf(result) shouldBe OK
        bodyOf(result) shouldBe Json.toJson(userDetails).toString()
      }

      "no user details are returned" in {
        val result = setupService(true, 401, Json.toJson(userDetails)).getUserDetails(50) {
          model => Future.successful(Ok(Json.toJson(model)))
        }(fakeRequestWithToken)

        statusOf(result) shouldBe OK
        bodyOf(result) shouldBe "null"
      }

      "an upstream auth exception occurs" in {
        val result = setupService(true, 500, Json.toJson(userDetails), "error message").getUserDetails(50) {
          model => Future.successful(Ok(Json.toJson(model)))
        }(fakeRequestWithToken)

        statusOf(result) shouldBe OK
        bodyOf(result) shouldBe "null"
      }
    }

    "return an exception" when {

      "a generic exception occurs" in {
        val result = setupService(false, 200, Json.toJson(userDetails), "generic error message").getUserDetails(50) {
          model => Future.successful(Ok(Json.toJson(model)))
        }(fakeRequestWithToken)

        the[Exception] thrownBy await(result) should have message "generic error message"
      }
    }
  }

  "Calling .login" should {

    "return a token string" when {

      "a 200 response is returned" in {
        val result = setupService(true, 200, message = "token").login(LoginModel("username", "password"))

        await(result) shouldBe Some("token")
      }
    }

    "return no token string" when {

      "a 401 response is returned" in {
        val result = setupService(true, 401, message = "token").login(LoginModel("username", "password"))

        await(result) shouldBe None
      }
    }

    "return an exception" when {

      "a different response is returned" in {
        val result = setupService(true, 500, message = "error message").login(LoginModel("username", "password"))

        the[UpstreamAuthException] thrownBy await(result) shouldBe UpstreamAuthException(500, "error message")
      }

      "an exception is returned" in {
        val result = setupService(false, 200, message = "generic error message").login(LoginModel("username", "password"))

        the[Exception] thrownBy await(result) should have message "generic error message"
      }
    }
  }

  "Calling .registerAndLogin" should {

    "return a token string" when {

      "a 200 response is returned" in {
        val result = setupService(true, 200, message = "token").registerAndLogin(UserModel("username", "email", "password"))

        await(result) shouldBe Some("token")
      }
    }

    "return no token string" when {

      "a 400 response is returned" in {
        val result = setupService(true, 400, message = "token").registerAndLogin(UserModel("username", "email", "password"))

        await(result) shouldBe None
      }
    }

    "return an exception" when {

      "a different response is returned" in {
        val result = setupService(true, 500, message = "error message").registerAndLogin(UserModel("username", "email", "password"))

        the[UpstreamAuthException] thrownBy await(result) shouldBe UpstreamAuthException(500, "error message")
      }

      "an exception is returned" in {
        val result = setupService(false, 200, message = "generic error message").registerAndLogin(UserModel("username", "email", "password"))

        the[Exception] thrownBy await(result) should have message "generic error message"
      }
    }
  }

  "Calling .updateEmail" should {

    "return a true" when {

      "a response of 204 is returned" in {
        val result = setupService(true, 204).updateEmail(UpdateIndividualDetailModel("name", "password", "value"))

        await(result) shouldBe true
      }
    }

    "return an UpstreamAuthException" when {

      "any other response is returned" in {
        val result = setupService(true, 200, message = "error message").updateEmail(UpdateIndividualDetailModel("name", "password", "value"))

        the[UpstreamAuthException] thrownBy await(result) shouldBe UpstreamAuthException(200, "error message")
      }
    }
  }

  "Calling .updatePassword" should {

    "return a true" when {

      "a response of 204 is returned" in {
        val result = setupService(true, 204).updatePassword(UpdateIndividualDetailModel("name", "password", "value"))

        await(result) shouldBe true
      }
    }

    "return an UpstreamAuthException" when {

      "any other response is returned" in {
        val result = setupService(true, 200, message = "error message").updatePassword(UpdateIndividualDetailModel("name", "password", "value"))

        the[UpstreamAuthException] thrownBy await(result) shouldBe UpstreamAuthException(200, "error message")
      }
    }
  }

  "Calling .updateDetails" should {

    "return a true" when {

      "successfully updating email and password" in {
        val result = setupService(true, 204)
          .updateDetails(UpdateDetailsModel("password", Some("newPassword"), Some("newEmail")), UserDetailsModel("username", "email", 50))(fakeRequestWithToken)

        await(result) shouldBe true
      }

      "successfully updating only the email" in {
        val result = setupService(true, 204)
          .updateDetails(UpdateDetailsModel("password", None, Some("newEmail")), UserDetailsModel("username", "email", 50))(fakeRequestWithToken)

        await(result) shouldBe true
      }

      "successfully updating only the password" in {
        val result = setupService(true, 204)
          .updateDetails(UpdateDetailsModel("password", Some("newPassword"), None), UserDetailsModel("username", "email", 50))(fakeRequestWithToken)

        await(result) shouldBe true
      }
    }

    "return a false" when {

      "attempting to update without a value" in {
        val result = setupService(true, 204)
          .updateDetails(UpdateDetailsModel("password", None, None), UserDetailsModel("username", "email", 50))(fakeRequestWithToken)

        await(result) shouldBe false
      }
    }
  }
}
