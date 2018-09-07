package helpers

import specs.UnitSpec

class HttpStatusHelperSpec extends UnitSpec {

  val statusMessageMap = Map (
    400 -> "Bad Request",
    401 -> "Unauthorised",
    403 -> "Forbidden",
    404 -> "Not Found",
    500 -> "Internal Server Error",
    502 -> "Unexpected Error",
    503 -> "Service Unavailable"
  )

  val authStatusMessageMap = Map (
    400 -> "The identifying details you provided are invalid, please try logging in again.",
    401 -> "You need to be logged in to access this resource.",
    403 -> "You are not authorised to access this resource.",
    404 -> "Something went wrong with the Authentication servers, please try again later.",
    500 -> "Something went wrong with the Authentication servers, please try again later.",
    502 -> "Something went wrong with the Authentication servers, please try again later.",
    503 -> "The Authentication servers are currently down and will be restored when they are ready."
  )

  val universeStatusMessageMap = Map (
    400 -> "The information you requested does not exist.",
    401 -> "You need to be logged in to access this resource.",
    403 -> "You are not authorised to access this resource.",
    404 -> "Something went wrong with the Universe servers, please try again later.",
    500 -> "Something went wrong with the Universe servers, please try again later.",
    502 -> "Something went wrong with the Universe servers, please try again later.",
    503 -> "The Universe servers are currently down and will be restored when they are ready."
  )

  "Calling .statusToString" should {

    statusMessageMap.foreach { entry =>

      s"return a message of ${entry._2}" in {
        HttpStatusHelper.statusToString(entry._1) shouldBe entry._2
      }
    }
  }

  "Calling .authStatusMessage" should {

    authStatusMessageMap.foreach { entry =>

      s"return the correct message for status ${entry._1}" in {
        HttpStatusHelper.authStatusMessage(entry._1) shouldBe entry._2
      }
    }
  }

  "Calling .universeStatusMessage" should {

    universeStatusMessageMap.foreach { entry =>

      s"return the correct message for status ${entry._1}" in {
        HttpStatusHelper.universeStatusMessage(entry._1) shouldBe entry._2
      }
    }
  }
}
