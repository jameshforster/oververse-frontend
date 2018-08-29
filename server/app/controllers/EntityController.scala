package controllers

import com.google.inject.Inject
import helpers.AuthLevelHelper
import models.entities.PlanetEntity
import models.{GalaxyModel, UserDetailsModel}
import models.location.Coordinates
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.{AuthService, UniverseService}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class EntityController @Inject()(cc: ControllerComponents,
                        authService: AuthService,
                        universeService: UniverseService,
                        override val messagesApi: MessagesApi) extends AbstractController(cc) with I18nSupport {

  def viewPlanet(galaxyName: String, galX: Int, galY: Int, sysX: Int, sysY: Int): Action[AnyContent] = Action.async { implicit request =>
    universeService.getGalaxyList flatMap { galaxies =>
      galaxies.find(_.galaxyName == galaxyName).fold[Future[Result]](Future.successful(BadRequest)) {
        case galaxyModel@GalaxyModel(_, _, true, false) => authService.getUserDetails(AuthLevelHelper.verified) {
          userDetails => loadPlanetViewer(galaxyModel, userDetails.get, Coordinates(galX, galY), Coordinates(sysX, sysY))
        }
        case galaxyModel@GalaxyModel(_, _, true, true) => authService.getUserDetails(AuthLevelHelper.moderator) {
          userDetails => loadPlanetViewer(galaxyModel, userDetails.get, Coordinates(galX, galY), Coordinates(sysX, sysY))
        }
        case galaxyModel => authService.getUserDetails(AuthLevelHelper.admin) {
          userDetails => loadPlanetViewer(galaxyModel, userDetails.get, Coordinates(galX, galY), Coordinates(sysX, sysY))
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
}
