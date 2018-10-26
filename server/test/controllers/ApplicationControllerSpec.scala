package controllers

import models.UserDetailsModel
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.mvc.{ControllerComponents, Result}
import services.AuthService
import specs.UnitSpec

import scala.concurrent.Future

class ApplicationControllerSpec extends UnitSpec with GuiceOneAppPerSuite {

  class TestSetup(userDetailsModel: Option[UserDetailsModel]) {
    val components: ControllerComponents = fakeApplication().injector.instanceOf[ControllerComponents]
    val authService: AuthService = spy(fakeApplication().injector.instanceOf[AuthService])

    doReturn(Future.successful(userDetailsModel), Seq.empty: _*)
      .when(authService).authorise(ArgumentMatchers.eq(50))(ArgumentMatchers.any())

    val controller = new ApplicationController(components, authService)
  }

  "Calling .index" should {

    "return a 200 with the index page" when {

      "logged in" in new TestSetup(Some(UserDetailsModel("name", "email", 50))) {
        val result: Future[Result] = controller.index(fakeRequestWithToken).run()

        statusOf(result) shouldBe 200
        bodyOf(result) shouldBe views.html.index("It works!", Some(UserDetailsModel("name", "email", 50)))(fakeRequestWithToken).body
        verify(authService).authorise(ArgumentMatchers.eq(50))(ArgumentMatchers.any())
      }

      "not logged in" in new TestSetup(None) {
        val result: Future[Result] = controller.index(fakeRequest).run()

        statusOf(result) shouldBe 200
        bodyOf(result) shouldBe views.html.index("It works!", None)(fakeRequest).body
        verify(authService).authorise(ArgumentMatchers.eq(50))(ArgumentMatchers.any())
      }
    }
  }
}
