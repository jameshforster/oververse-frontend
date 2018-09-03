import com.google.inject.{Inject, Singleton}
import models.exceptions.{UpstreamAuthException, UpstreamUniverseException}
import play.api.Logger
import play.api.http.HttpErrorHandler
import play.api.mvc.{RequestHeader, Result}
import play.api.mvc.Results._

import scala.concurrent.Future

@Singleton
class ErrorHandler @Inject()() extends HttpErrorHandler {

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    Future.successful(Status(statusCode)("A client error occurred: " + message))
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    exception match {
      case e: UpstreamAuthException => handleAuthException(e)
      case e: UpstreamUniverseException => handleUniverseException(e)
      case _ => Future.successful(InternalServerError(views.html.exceptions.genericException()))
    }
  }

  private def handleUniverseException(upstreamUniverseException: UpstreamUniverseException): Future[Result] = {
    Logger.error(upstreamUniverseException.message, upstreamUniverseException)
    Future.successful(Status(upstreamUniverseException.status)(views.html.exceptions.universeException(upstreamUniverseException.status)))
  }

  private def handleAuthException(upstreamAuthException: UpstreamAuthException): Future[Result] = {
    Logger.error(upstreamAuthException.message, upstreamAuthException)
    Future.successful(Status(upstreamAuthException.status)(views.html.exceptions.authException(upstreamAuthException.status)))
  }
}
