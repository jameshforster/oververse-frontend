package services

import com.google.inject.Inject
import connectors.UniverseConnector
import models.GalaxyModel
import models.entities.{Entity, StarEntity}
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
}
