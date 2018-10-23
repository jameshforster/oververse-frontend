package controllers

import forms.UpdateDetailsForm
import models.{UpdateDetailsModel, UserDetailsModel}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.Form
import play.api.i18n.{Lang, Messages, MessagesApi, MessagesProvider}
import play.api.mvc.{ControllerComponents, Result}
import services.AuthService
import specs.UnitSpec

import scala.concurrent.Future

class AccountControllerSpec extends UnitSpec with GuiceOneAppPerSuite {

  class TestController(updateSuccess: Boolean = true) {
    val components: ControllerComponents = fakeApplication().injector.instanceOf[ControllerComponents]
    val authService: AuthService = spy(fakeApplication().injector.instanceOf[AuthService])
    val messagesApi: MessagesApi = fakeApplication().injector.instanceOf[MessagesApi]

    implicit val messagesProvider: MessagesProvider = new MessagesProvider {
      override def messages: Messages = messagesApi.preferred(Seq(Lang("En")))
    }

    doReturn(Future.successful(Some(UserDetailsModel("name", "email", 50))), Seq.empty: _*)
      .when(authService).authorise(ArgumentMatchers.eq(50))(ArgumentMatchers.any())

    doReturn(Future.successful(updateSuccess), Seq.empty: _*)
      .when(authService).updateDetails(ArgumentMatchers.any[UpdateDetailsModel], ArgumentMatchers.any[UserDetailsModel])(ArgumentMatchers.any())

    val controller = new AccountController(components, authService, messagesApi)
  }

  "Calling .getAccount" should {

    "return a status of 200 and load the account page" when {

      "the user is logged in via the authorise method" in new TestController {
        val result: Future[Result] = controller.getAccount(fakeRequestWithTokenAndCSRF).run()

        statusOf(result) shouldBe 200
        bodyOf(result) shouldBe views.html.account.account(UserDetailsModel("name", "email", 50), UpdateDetailsForm.updateDetailsForm)(fakeRequestWithTokenAndCSRF.withBody(), implicitly).body
        verify(authService).authorise(ArgumentMatchers.eq(50))(ArgumentMatchers.any())
      }
    }
  }

  "Calling .updateDetails" should {

    "return a status of 303 for redirecting to the account page" when {

      "the user is logged in via the authorise method and successfully updates their details" in new TestController {
        val result: Future[Result] = controller.updateDetails(fakePostRequest(
          "password" -> "password",
          "newPassword" -> "newPassword",
          "newEmail" -> "newEmail@example.com"))

        statusOf(result) shouldBe 303
        redirectLocation(result) shouldBe Some(controllers.routes.AccountController.getAccount().url)
        verify(authService).authorise(ArgumentMatchers.eq(50))(ArgumentMatchers.any())
        verify(authService).updateDetails(ArgumentMatchers.any[UpdateDetailsModel], ArgumentMatchers.any[UserDetailsModel])(ArgumentMatchers.any())
      }
    }

    "return a status of 400 and reload the account page" when {

      "the form validation fails" in new TestController {
        val result: Future[Result] = controller.updateDetails(fakePostRequest(
          "password" -> "",
          "newPassword" -> "newPassword",
          "newEmail" -> "newEmail@example.com"))

        val failedForm: Form[UpdateDetailsModel] = UpdateDetailsForm.updateDetailsForm.bind(Map(
          "password" -> "",
          "newPassword" -> "newPassword",
          "newEmail" -> "newEmail@example.com"
        ))

        statusOf(result) shouldBe 400
        bodyOf(result) shouldBe views.html.account.account(UserDetailsModel("name", "email", 50), failedForm)(fakeRequestWithTokenAndCSRF.withBody(), implicitly).body
        verify(authService).authorise(ArgumentMatchers.eq(50))(ArgumentMatchers.any())
        verify(authService, never()).updateDetails(ArgumentMatchers.any[UpdateDetailsModel], ArgumentMatchers.any[UserDetailsModel])(ArgumentMatchers.any())
      }

      "the user is logged in via authorise but the update returns a failure" in new TestController(false) {
        val result: Future[Result] = controller.updateDetails(fakePostRequest(
          "password" -> "password",
          "newPassword" -> "newPassword",
          "newEmail" -> "newEmail@example.com"))

        val failedForm: Form[UpdateDetailsModel] =  UpdateDetailsForm.updateDetailsForm.bind(Map(
          "password" -> "password",
          "newPassword" -> "newPassword",
          "newEmail" -> "newEmail@example.com"
        )).withError("", "No details to update!")

        statusOf(result) shouldBe 400
        bodyOf(result) shouldBe views.html.account.account(UserDetailsModel("name", "email", 50), failedForm)(fakeRequestWithTokenAndCSRF.withBody(), implicitly).body
        verify(authService).authorise(ArgumentMatchers.eq(50))(ArgumentMatchers.any())
        verify(authService).updateDetails(ArgumentMatchers.any[UpdateDetailsModel], ArgumentMatchers.any[UserDetailsModel])(ArgumentMatchers.any())
      }
    }
  }
}
