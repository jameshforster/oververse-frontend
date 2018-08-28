package controllers

import com.google.inject.Inject
import helpers.AuthLevelHelper
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.{AuthService, UniverseService}

import scala.concurrent.ExecutionContext.Implicits.global

class GameController @Inject()(cc: ControllerComponents,
                               authService: AuthService,
                               universeService: UniverseService,
                               override val messagesApi: MessagesApi) extends AbstractController(cc) with I18nSupport {


  def main(galaxyName: String): Action[AnyContent] = Action.async { implicit request =>
    authService.authoriseOrLogin(100) { userDetailsModel =>
      universeService.getGalaxy(galaxyName) map {
        case Some(galaxy) =>
          val authorised = if (galaxy.active && !galaxy.test) true
          else if (galaxy.active) userDetailsModel.authLevel > AuthLevelHelper.moderator
          else userDetailsModel.authLevel > AuthLevelHelper.admin

          if (authorised) Ok(views.html.game.mainGame(userDetailsModel, galaxyName))
          else Forbidden("")
        case _ => BadRequest("")
      }
    }
  }
}
