package models.requests

import models.location.Coordinates
import play.api.libs.json.{Json, OFormat}

case class UniverseQueryRequest(galaxyName: String,
                                select: String,
                                name: Option[String] = None,
                                category: Option[String] = None,
                                galacticCoordinates: Option[Coordinates] = None)

object UniverseQueryRequest {
  implicit val formats: OFormat[UniverseQueryRequest] = Json.format[UniverseQueryRequest]
}
