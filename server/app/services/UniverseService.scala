package services

import com.google.inject.Inject
import connectors.UniverseConnector
import models.{GalaxyModel, StarSystem}
import models.entities.{Entity, StarEntity}
import models.location.Coordinates
import models.requests.UniverseQueryRequest
import play.api.mvc.RequestImplicits

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class UniverseService @Inject()(universeConnector: UniverseConnector) extends RequestImplicits {

  def getGalaxyList: Future[Seq[GalaxyModel]] = {
    universeConnector.getGalaxies map {
      case r if r.status == 200 => r.json.as[Seq[GalaxyModel]]
      case _ => Seq()
    }
  }

  def getGalaxy(name: String): Future[Option[GalaxyModel]] = {
    getGalaxyList.map(_.find(_.galaxyName == name))
  }

  def getAllGalaxyStars(galaxyName: String): Future[Seq[StarEntity]] = {
    universeConnector.getStars(UniverseQueryRequest(galaxyName, "all")) map  {
      case r if r.status == 200 =>
        r.json.as[Seq[Entity]] flatMap {
          case e: StarEntity => Some(e)
          case _ => None
        }
      case _ => ???
    }
  }

  def getStarSystem(galaxyName: String, coordinates: Coordinates): Future[Option[StarSystem]] = {
    universeConnector.getSystem(UniverseQueryRequest(galaxyName, "all", galacticCoordinates = Some(coordinates))) map {
      case r if r.status == 200 =>
        r.json.as[Seq[StarSystem]].headOption
      case _ => ???
    }
  }

  def getStar(galaxyName: String, coordinates: Coordinates): Future[Option[StarEntity]] = {
    universeConnector.getStars(UniverseQueryRequest(galaxyName, "location", galacticCoordinates = Some(coordinates))) map {
      case r if r.status == 200 =>
        r.json.as[Seq[Entity]].headOption map {_.asInstanceOf[StarEntity]}
      case _ => ???
    }
  }
}
