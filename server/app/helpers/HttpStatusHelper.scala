package helpers

object HttpStatusHelper {

  def statusToString(status: Int): String = {
    status match {
      case 400 => "Bad Request"
      case 401 => "Unauthorised"
      case 403 => "Forbidden"
      case 404 => "Not Found"
      case 500 => "Internal Server Error"
      case 503 => "Service Unavailable"
      case _ => "Unexpected Error"
    }
  }

  def authStatusMessage(status: Int): String = {
    status match {
      case 400 => "The identifying details you provided are invalid, please try logging in again."
      case 401 => "You need to be logged in to access this resource."
      case 403 => "You are not authorised to access this resource."
      case 503 => "The Authentication servers are currently down and will be restored when they are ready."
      case _ => "Something went wrong with the Authentication servers, please try again later."
    }
  }

  def universeStatusMessage(status: Int): String = {
    status match {
      case 400 => "The information you requested does not exist."
      case 401 => "You need to be logged in to access this resource."
      case 403 => "You are not authorised to access this resource."
      case 503 => "The Universe servers are currently down and will be restored when they are ready."
      case _ => "Something went wrong with the Universe servers, please try again later."
    }
  }
}
