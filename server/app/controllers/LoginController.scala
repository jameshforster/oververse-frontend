package controllers

import com.google.inject.Singleton
import forms.{LoginForm, RegisterForm}
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.AuthService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class LoginController @Inject()(cc: ControllerComponents, authService: AuthService, override val messagesApi: MessagesApi) extends AbstractController(cc) with I18nSupport {

  val getRegister: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(views.html.account.register(RegisterForm.registerForm)))
  }

  val postRegister: Action[AnyContent] = Action.async { implicit request =>
    RegisterForm.registerForm.bindFromRequest().fold(
      errors => Future.successful(BadRequest(views.html.account.register(errors))),
      success => authService.registerAndLogin(success).map {
        case Some(token) => Redirect(controllers.routes.AccountController.getAccount().url).addingToSession(("authToken", token))
        case _ =>
          val errors = RegisterForm.registerForm.fill(success).withError("", "Username already in use")
          BadRequest(views.html.account.register(errors))
      }
    )
  }

  val getLogin: String => Action[AnyContent] = destination => Action.async { implicit request =>
    Future.successful(Ok(views.html.account.login(LoginForm.loginForm, destination)))
  }

  val postLogin: String => Action[AnyContent] = destination => Action.async { implicit request =>
    LoginForm.loginForm.bindFromRequest().fold(
      errors => Future.successful(BadRequest(views.html.account.login(errors, destination))),
      success => authService.login(success).map {
        case Some(token) => Redirect(destination).addingToSession(("authToken", token))
        case _ =>
          val errors = LoginForm.loginForm.fill(success).withError("", "Invalid login details")
          BadRequest(views.html.account.login(errors, destination))
      }
    )
  }

  val logout: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Redirect(controllers.routes.ApplicationController.index().url).removingFromSession("authToken"))
  }
}
