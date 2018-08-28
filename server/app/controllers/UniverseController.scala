package controllers

import com.google.inject.{Inject, Singleton}
import helpers.AuthLevelHelper
import models.{GalaxyModel, UserDetailsModel}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.{AuthService, UniverseService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class UniverseController @Inject()(cc: ControllerComponents,
                                   authService: AuthService,
                                   universeService: UniverseService,
                                   override val messagesApi: MessagesApi) extends AbstractController(cc) with I18nSupport {

  val getUniverse: Action[AnyContent] = Action.async { implicit request =>
    authService.getUserDetails(50) { userDetails =>
      universeService.getGalaxyList map { galaxies =>
        val auth = userDetails.map(_.authLevel).getOrElse(50)
        val filteredGalaxies = if (auth >= AuthLevelHelper.admin) galaxies
        else if (auth >= AuthLevelHelper.moderator) galaxies.filterNot(_.active)
        else galaxies.filterNot(_.test)
        Ok(views.html.universe.universe(userDetails, filteredGalaxies))
      }
    }
  }

  def galaxyMap(galaxyName: String, quadrant: Int): Action[AnyContent] = Action.async { implicit request =>
    universeService.getGalaxyList flatMap { galaxies =>
      galaxies.find(_.galaxyName == galaxyName).fold[Future[Result]](Future.successful(BadRequest)) {
        case galaxyModel@GalaxyModel(_, _, true, false) => authService.getUserDetails(AuthLevelHelper.verified) {
          userDetails => loadGalaxyMap(galaxyModel, userDetails.get, quadrant)
        }
        case galaxyModel@GalaxyModel(_, _, true, true) => authService.getUserDetails(AuthLevelHelper.moderator) {
          userDetails => loadGalaxyMap(galaxyModel, userDetails.get, quadrant)
        }
        case galaxyModel => authService.getUserDetails(AuthLevelHelper.admin) {
          userDetails => loadGalaxyMap(galaxyModel, userDetails.get, quadrant)
        }
      }
    }
  }

  private def loadGalaxyMap(galaxyModel: GalaxyModel, userDetails: UserDetailsModel, quadrant: Int)(implicit request: Request[_]): Future[Result] = {
    universeService.getAllGalaxyStars(galaxyModel.galaxyName) map { stars =>
      Ok(views.html.universe.galaxyMap(galaxyModel, stars, userDetails, quadrant))
    }
  }
}
