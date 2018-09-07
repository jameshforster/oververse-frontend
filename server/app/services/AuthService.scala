package services

import com.google.inject.{Inject, Singleton}
import connectors.AuthConnector
import models._
import models.exceptions.UpstreamAuthException
import play.api.Logger
import play.api.mvc.Results._
import play.api.mvc.{Request, RequestImplicits, Result}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AuthService @Inject()(authConnector: AuthConnector) extends RequestImplicits {

  def authoriseOrLogin(requiredAuth: Int)(authorisedAction: UserDetailsModel => Future[Result])(implicit request: Request[_]): Future[Result] = {
    authorise(requiredAuth) flatMap {
      case Some(user) => authorisedAction(user)
      case None => Future.successful(Redirect(controllers.routes.LoginController.getLogin(request.path)))
    }
  }

  def getUserDetails(requiredAuth: Int)(action: Option[UserDetailsModel] => Future[Result])(implicit request: Request[_]): Future[Result] = {
    authorise(requiredAuth).recover {
      case _: UpstreamAuthException => None
    } flatMap action
  }

  def registerAndLogin(userModel: UserModel): Future[Option[String]] = {
    authConnector.register(userModel).flatMap {
      case r if r.status == 200 => login(LoginModel(userModel.username, userModel.password))
      case r if r.status == 400 => Future.successful(None)
      case r =>
        Logger.error(s"Unexpected response of ${r.status} when registering.")
        throw UpstreamAuthException(r.status, r.body)
    }
  }

  def login(loginModel: LoginModel): Future[Option[String]] = {
    authConnector.login(loginModel) map {
      case r if r.status == 200 => Some(r.body)
      case r if r.status == 401 => None
      case r =>
        Logger.error(s"Unexpected response of ${r.status} when logging in.")
        throw UpstreamAuthException(r.status, r.body)
    }
  }

  def authorise(requiredAuth: Int)(implicit request: Request[_]): Future[Option[UserDetailsModel]] = {
    request2session.get("authToken") match {
      case Some(token) => authConnector.authorise(requiredAuth, token) map {
        case r if r.status == 200 => Some(r.json.as[UserDetailsModel])
        case r if r.status == 401 => None
        case r =>
          Logger.error(s"Unexpected response of ${r.status} when logging in.")
          throw UpstreamAuthException(r.status, r.body)
      }
      case _ => Future.successful(None)
    }
  }

  def updateDetails(updateDetailsModel: UpdateDetailsModel, userDetailsModel: UserDetailsModel)(implicit request: Request[_]): Future[Boolean] = {
    updateDetailsModel match {
      case UpdateDetailsModel(_, Some(newPassword), Some(newEmail)) =>
       updateEmail(UpdateIndividualDetailModel(userDetailsModel.username, updateDetailsModel.password, newEmail)) flatMap { _ =>
         updatePassword(UpdateIndividualDetailModel(userDetailsModel.username, updateDetailsModel.password, newPassword)) map (_ => true)
        }
      case UpdateDetailsModel(_, Some(newPassword), _) =>
       updatePassword(UpdateIndividualDetailModel(userDetailsModel.username, updateDetailsModel.password, newPassword)).map (_ => true)
      case UpdateDetailsModel(_, _, Some(newEmail)) =>
       updateEmail(UpdateIndividualDetailModel(userDetailsModel.username, updateDetailsModel.password, newEmail)) map (_ => true)
      case _ => Future.successful(false)
    }
  }

  private[services] def updateEmail(updateIndividualDetailModel: UpdateIndividualDetailModel): Future[Boolean] = {
    authConnector.updateEmail(updateIndividualDetailModel).map {
      case r if r.status == 204 => true
      case r =>
        Logger.error(s"Unexpected response of ${r.status} when updating email.")
        throw UpstreamAuthException(r.status, r.body)
    }
  }

  private[services] def updatePassword(updateIndividualDetailModel: UpdateIndividualDetailModel): Future[Boolean] = {
    authConnector.updatePassword(updateIndividualDetailModel).map {
      case r if r.status == 204 => true
      case r =>
        Logger.error(s"Unexpected response of ${r.status} when updating password.")
        throw UpstreamAuthException(r.status, r.body)
    }
  }
}

