package controllers

import com.google.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.{AuthService, UniverseService}
import models.UserDetailsModel._
import models.exceptions.UpstreamUniverseException

import scala.concurrent.ExecutionContext.Implicits.global

class GameController @Inject()(cc: ControllerComponents,
                               authService: AuthService,
                               universeService: UniverseService,
                               override val messagesApi: MessagesApi) extends AbstractController(cc) with I18nSupport {


  def main(galaxyName: String): Action[AnyContent] = Action.async { implicit request =>
    authService.authoriseOrLogin(verified) { userDetailsModel =>
      universeService.getGalaxy(galaxyName) map {
        case Some(galaxy) =>
          val authorised = if (galaxy.active && !galaxy.test) true
          else if (galaxy.active) userDetailsModel.authLevel > moderator
          else userDetailsModel.authLevel > admin

          if (authorised) Ok(views.html.game.mainGame(userDetailsModel, galaxyName))
          else throw UpstreamUniverseException(403, s"Insufficient permissions to access $galaxyName")
        case _ => throw UpstreamUniverseException(400, s"No galaxy of name: $galaxyName found")
      }
    }
  }
}
