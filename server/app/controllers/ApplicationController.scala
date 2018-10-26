package controllers

import javax.inject._
import messages.SharedMessages
import play.api.mvc._
import services.AuthService
import models.UserDetailsModel._

import scala.concurrent.Future

@Singleton
class ApplicationController @Inject()(cc: ControllerComponents, authService: AuthService) extends AbstractController(cc) {

  val index: Action[AnyContent] = Action.async { implicit request =>
    authService.getUserDetails(unverified) { userDetails =>
      Future.successful(Ok(views.html.index(SharedMessages.itWorks, userDetails)))
    }
  }
}
