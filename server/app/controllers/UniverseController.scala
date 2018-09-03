package controllers

import com.google.inject.{Inject, Singleton}
import models.location.Coordinates
import models.{GalaxyModel, UserDetailsModel}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.{AuthService, UniverseService}
import models.UserDetailsModel._
import models.exceptions.UpstreamUniverseException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class UniverseController @Inject()(cc: ControllerComponents,
                                   authService: AuthService,
                                   universeService: UniverseService,
                                   override val messagesApi: MessagesApi) extends AbstractController(cc) with I18nSupport {

  val getUniverse: Action[AnyContent] = Action.async { implicit request =>
    authService.getUserDetails(unverified) { userDetails =>
      universeService.getGalaxyList map { galaxies =>
        val auth = userDetails.map(_.authLevel).getOrElse(unverified)
        val filteredGalaxies = if (auth >= admin) galaxies
        else galaxies.filter(_.active)
        Ok(views.html.universe.universe(userDetails, filteredGalaxies))
      }
    }
  }

  def galaxyMap(galaxyName: String, quadrant: Int): Action[AnyContent] = Action.async { implicit request =>
    universeService.getGalaxyList flatMap { galaxies =>
      galaxies.find(_.galaxyName == galaxyName).fold[Future[Result]](Future.successful(BadRequest)) {
        case galaxyModel@GalaxyModel(_, _, true, false) => authService.getUserDetails(verified) {
          userDetails => loadGalaxyMap(galaxyModel, userDetails.get, quadrant)
        }
        case galaxyModel@GalaxyModel(_, _, true, true) => authService.getUserDetails(moderator) {
          userDetails => loadGalaxyMap(galaxyModel, userDetails.get, quadrant)
        }
        case galaxyModel => authService.getUserDetails(admin) {
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

  def systemMap(galaxyName: String, xCoordinate: Int, yCoordinate: Int): Action[AnyContent] = Action.async { implicit request =>
    universeService.getGalaxyList flatMap { galaxies =>
      galaxies.find(_.galaxyName == galaxyName).fold[Future[Result]](Future.successful(BadRequest)) {
        case galaxyModel@GalaxyModel(_, _, true, false) => authService.getUserDetails(verified) {
          userDetails => loadStarSystemMap(galaxyModel, userDetails.get, Coordinates(xCoordinate, yCoordinate))
        }
        case galaxyModel@GalaxyModel(_, _, true, true) => authService.getUserDetails(moderator) {
          userDetails => loadStarSystemMap(galaxyModel, userDetails.get, Coordinates(xCoordinate, yCoordinate))
        }
        case galaxyModel => authService.getUserDetails(admin) {
          userDetails => loadStarSystemMap(galaxyModel, userDetails.get, Coordinates(xCoordinate, yCoordinate))
        }
      }
    }
  }

  private def loadStarSystemMap(galaxyModel: GalaxyModel, userDetailsModel: UserDetailsModel, coordinates: Coordinates)(implicit request: Request[_]): Future[Result] = {
    universeService.getStarSystem(galaxyModel.galaxyName, coordinates) map {
      case Some(system) => Ok(views.html.universe.starMap(system, userDetailsModel))
      case None => throw UpstreamUniverseException(400, s"No star system found at coordinates: $coordinates for galaxy: ${galaxyModel.galaxyName}")
    }
  }
}
