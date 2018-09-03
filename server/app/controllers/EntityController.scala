package controllers

import com.google.inject.Inject
import models.UserDetailsModel._
import models.entities.PlanetEntity
import models.location.Coordinates
import models.{GalaxyModel, UserDetailsModel}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.{AuthService, UniverseService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EntityController @Inject()(cc: ControllerComponents,
                                 authService: AuthService,
                                 universeService: UniverseService,
                                 override val messagesApi: MessagesApi) extends AbstractController(cc) with I18nSupport {

  def viewPlanet(galaxyName: String, galX: Int, galY: Int, sysX: Int, sysY: Int): Action[AnyContent] = Action.async { implicit request =>
    universeService.getGalaxyList flatMap { galaxies =>
      galaxies.find(_.galaxyName == galaxyName).fold[Future[Result]](Future.successful(BadRequest)) {
        case galaxyModel@GalaxyModel(_, _, true, false) => authService.getUserDetails(verified) {
          userDetails => loadPlanetViewer(galaxyModel, userDetails.get, Coordinates(galX, galY), Coordinates(sysX, sysY))
        }
        case galaxyModel@GalaxyModel(_, _, true, true) => authService.getUserDetails(moderator) {
          userDetails => loadPlanetViewer(galaxyModel, userDetails.get, Coordinates(galX, galY), Coordinates(sysX, sysY))
        }
        case galaxyModel => authService.getUserDetails(admin) {
          userDetails => loadPlanetViewer(galaxyModel, userDetails.get, Coordinates(galX, galY), Coordinates(sysX, sysY))
        }
      }
    }
  }

  def viewStar(galaxyName: String, x: Int, y: Int): Action[AnyContent] = Action.async { implicit request =>
    universeService.getGalaxyList flatMap { galaxies =>
      galaxies.find(_.galaxyName == galaxyName).fold[Future[Result]](Future.successful(BadRequest)) {
        case galaxyModel@GalaxyModel(_, _, true, false) => authService.getUserDetails(verified) {
          userDetails => loadStarViewer(galaxyModel, userDetails.get, Coordinates(x, y))
        }
        case galaxyModel@GalaxyModel(_, _, true, true) => authService.getUserDetails(moderator) {
          userDetails => loadStarViewer(galaxyModel, userDetails.get, Coordinates(x, y))
        }
        case galaxyModel => authService.getUserDetails(admin) {
          userDetails => loadStarViewer(galaxyModel, userDetails.get, Coordinates(x, y))
        }
      }
    }
  }

  def loadPlanetViewer(galaxyModel: GalaxyModel,
                       userDetails: UserDetailsModel,
                       galacticCoordinates: Coordinates,
                       orbitalCoordinates: Coordinates)(implicit request: Request[_]): Future[Result] = {
    universeService.getStarSystem(galaxyModel.galaxyName, galacticCoordinates) map {
      case Some(system) => system.majorOrbitals.find(_.location.system == orbitalCoordinates).fold(BadRequest("")) { planet =>
        Ok(views.html.universe.entityViewers.planetEntityViewer(planet.asInstanceOf[PlanetEntity], userDetails))
      }
      case _ => BadRequest("")
    }
  }

  def loadStarViewer(galaxyModel: GalaxyModel,
                     userDetailsModel: UserDetailsModel,
                     coordinates: Coordinates)(implicit request: Request[_]): Future[Result] = {
    universeService.getStar(galaxyModel.galaxyName, coordinates) map {
      case Some(star) =>
        Ok(views.html.universe.entityViewers.starEntityViewer(star, userDetailsModel))
      case _ => BadRequest("")
    }
  }
}
