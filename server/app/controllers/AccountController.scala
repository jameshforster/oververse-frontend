package controllers

import forms.UpdateDetailsForm
import javax.inject.{Inject, Singleton}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.AuthService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AccountController @Inject()(cc: ControllerComponents, authService: AuthService, override val messagesApi: MessagesApi) extends AbstractController(cc) with I18nSupport {

  val getAccount: Action[AnyContent] = Action.async { implicit request =>
    authService.authoriseOrLogin(50) { model =>
      Future.successful(Ok(views.html.account.account(model, UpdateDetailsForm.updateDetailsForm)))
    }
  }

  val updateDetails: Action[AnyContent] = Action.async { implicit request =>
    authService.authoriseOrLogin(50) { model =>
      UpdateDetailsForm.updateDetailsForm.bindFromRequest().fold(
        errors => Future.successful(BadRequest(views.html.account.account(model, errors))),
        success => authService.updateDetails(success, model) map {
          case true => Redirect(controllers.routes.AccountController.getAccount())
          case false => BadRequest(views.html.account.account(model, UpdateDetailsForm.updateDetailsForm.fill(success)
            .withError("", "No details to update!")))
        }
      )
    }
  }
}
