package connectors

import com.google.inject.{Inject, Singleton}
import conf.AppConfig
import models.exceptions.UpstreamUniverseException
import models.requests.UniverseQueryRequest
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.Future

@Singleton
class UniverseConnector @Inject()(ws: WSClient, appConfig: AppConfig) {

  def getGalaxies: Future[WSResponse] = {
    ws.url(appConfig.universeUrl + "/universe/query/galaxies").get().recover {
      case e: Exception => throw UpstreamUniverseException(404, e.getMessage)
    }
  }

  def getStars(universeQueryRequest: UniverseQueryRequest): Future[WSResponse] = {
    ws.url(appConfig.universeUrl + "/universe/query/star").post(Json.toJson(universeQueryRequest)).recover {
      case e: Exception => throw UpstreamUniverseException(404, e.getMessage)
    }
  }

  def getSystem(universeQueryRequest: UniverseQueryRequest): Future[WSResponse] = {
    ws.url(appConfig.universeUrl + "/universe/query/system").post(Json.toJson(universeQueryRequest)).recover {
      case e: Exception => throw UpstreamUniverseException(404, e.getMessage)
    }
  }
}
