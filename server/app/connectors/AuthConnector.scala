package connectors

import com.google.inject.{Inject, Singleton}
import conf.AppConfig
import models.exceptions.UpstreamAuthException
import models.{LoginModel, UpdateIndividualDetailModel, UserModel}
import play.api.libs.json.Json
import play.api.libs.ws._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AuthConnector @Inject()(ws: WSClient, appConfig: AppConfig) {

  def register(userModel: UserModel): Future[WSResponse] = {
    ws.url(appConfig.authUrl + "/authentication/user/register").post(Json.toJson(userModel)).recover {
      case e: Exception => throw UpstreamAuthException(404, e.getMessage)
    }
  }

  def login(loginModel: LoginModel): Future[WSResponse] = {
    ws.url(appConfig.authUrl + "/authentication/user/login").post(Json.toJson(loginModel)).recover {
      case e: Exception => throw UpstreamAuthException(404, e.getMessage)
    }
  }

  def authorise(requiredLevel: Int, authToken: String): Future[WSResponse] = {
    ws.url(appConfig.authUrl + s"/authentication/user/authorise/$requiredLevel").post(Json.toJson(authToken)).recover {
      case e: Exception => throw UpstreamAuthException(404, e.getMessage)
    }
  }

  def updateEmail(individualDetailModel: UpdateIndividualDetailModel): Future[WSResponse] = {
    ws.url(appConfig.authUrl + s"/authentication/user/update-email").post(Json.toJson(individualDetailModel)).recover {
      case e: Exception => throw UpstreamAuthException(404, e.getMessage)
    }
  }

  def updatePassword(individualDetailModel: UpdateIndividualDetailModel): Future[WSResponse] = {
    ws.url(appConfig.authUrl + s"/authentication/user/change-password").post(Json.toJson(individualDetailModel)).recover {
      case e: Exception => throw UpstreamAuthException(404, e.getMessage)
    }
  }
}
