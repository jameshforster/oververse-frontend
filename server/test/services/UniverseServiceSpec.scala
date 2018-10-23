package services

import connectors.UniverseConnector
import models.{GalaxyModel, StarSystem}
import models.attributes.Attributes
import models.entities.{Entity, PlanetEntity, StarEntity}
import models.exceptions.UpstreamUniverseException
import models.location.Coordinates
import models.requests.UniverseQueryRequest
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSResponse
import specs.UnitSpec

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class UniverseServiceSpec extends UnitSpec {

  def setupService(status: Int, errorResponse: Try[Unit] = Success(), jsonResponse: Option[JsValue] = None, bodyResponse: Option[String] = None): UniverseService = {
    val mockConnector = mock[UniverseConnector]
    val mockResponse = mock[WSResponse]

    when(mockResponse.status).thenReturn(status)
    jsonResponse.map(json => when(mockResponse.json).thenReturn(json))
    bodyResponse.map(body => when(mockResponse.body).thenReturn(body))

    when(mockConnector.getGalaxies).thenReturn {
      errorResponse match {
        case Success(_) => Future.successful(mockResponse)
        case Failure(error) => Future.failed(error)
      }
    }

    when(mockConnector.getStars(ArgumentMatchers.any[UniverseQueryRequest])).thenReturn {
      errorResponse match {
        case Success(_) => Future.successful(mockResponse)
        case Failure(error) => Future.failed(error)
      }
    }

    when(mockConnector.getSystem(ArgumentMatchers.any[UniverseQueryRequest])).thenReturn {
      errorResponse match {
        case Success(_) => Future.successful(mockResponse)
        case Failure(error) => Future.failed(error)
      }
    }

    new UniverseService(mockConnector)
  }

  val galaxyList = Seq(GalaxyModel("name1", 5, false, true), GalaxyModel("name2", 3, true, false))
  val starAttributes = Attributes(Map(
    "colour" -> Json.toJson("Yellow"),
    "size" -> Json.toJson(1)
  ))
  val planetAttributes = Attributes(Map(
    "size" -> Json.toJson(1),
    "atmosphere" -> Json.toJson(1),
    "geology" -> Json.toJson(1),
    "biosphere" -> Json.toJson(1),
    "toxicity" -> Json.toJson(1),
    "radioactivity" -> Json.toJson(1),
    "minerals" -> Json.toJson(1),
    "danger" -> Json.toJson(1),
    "breathable" -> Json.toJson(1),
    "water" -> Json.toJson(1),
    "temperature" -> Json.toJson(1),
    "planetType" -> Json.toJson("Barren")
  ))

  def star(name: String) = StarEntity("1", "galaxyName", name, starAttributes, Coordinates(1, 2), 100)

  def planet(name: String) = PlanetEntity("2", "galaxyName", name, planetAttributes, Coordinates(1, 2), Coordinates(3, 4), 10)

  "Calling .getGalaxyList" should {

    "return a list of galaxies" when {

      "a 200 response is returned" in {
        val result = setupService(200, jsonResponse = Some(Json.toJson(galaxyList))).getGalaxyList

        await(result) shouldBe galaxyList
      }
    }

    "return an empty sequence" when {

      "a 200 response with an empty body is returned" in {
        val result = setupService(200, jsonResponse = Some(Json.toJson(Seq.empty[GalaxyModel]))).getGalaxyList

        await(result) shouldBe Seq()
      }

      "any other response is returned" in {
        val result = setupService(500, jsonResponse = Some(Json.toJson(Seq(galaxyList)))).getGalaxyList

        await(result) shouldBe Seq()
      }
    }
  }

  "Calling .getGalaxy" should {

    "return a matching galaxy" when {

      "one is found" in {
        val result = setupService(200, jsonResponse = Some(Json.toJson(galaxyList))).getGalaxy("name2")

        await(result) shouldBe Some(GalaxyModel("name2", 3, true, false))
      }
    }

    "return no matching galaxy" when {

      "no galaxies are returned" in {
        val result = setupService(200, jsonResponse = Some(Json.toJson(Seq.empty[GalaxyModel]))).getGalaxy("name1")

        await(result) shouldBe None
      }

      "no matching galaxies are found" in {
        val result = setupService(200, jsonResponse = Some(Json.toJson(galaxyList))).getGalaxy("name3")

        await(result) shouldBe None
      }
    }
  }

  "Calling .getAllGalaxyStars" should {

    "return all star entities" when {

      "a sequence of stars and non-stars is returned" in {
        val json = Seq(star("star1"), planet("planet1"), planet("planet2"), star("star2"), star("star3"))
        val result = setupService(200, jsonResponse = Some(Json.toJson(json))).getAllGalaxyStars("galaxyName")

        await(result) shouldBe Seq(star("star1"), star("star2"), star("star3"))
      }
    }

    "return no star entities" when {

      "a sequence containing no stars is returned" in {
        val json = Seq(planet("planet1"), planet("planet2"), planet("planet3"))
        val result = setupService(200, jsonResponse = Some(Json.toJson(json))).getAllGalaxyStars("galaxyName")

        await(result) shouldBe Seq()
      }
    }

    "return an upstream universe exception" when {

      "a non 200 response is returned" in {
        val result = setupService(500, bodyResponse = Some("error message")).getAllGalaxyStars("galaxyName")

        the[UpstreamUniverseException] thrownBy await(result) shouldBe UpstreamUniverseException(500, "error message")
      }
    }
  }

  "Calling .getStarSystem" should {

    "return a star system" when {
      val system = StarSystem(star("starName"), Seq(planet("planetName1"), planet("planetName2")), Seq(), Seq())
      val system2 = StarSystem(star("starName2"), Seq(planet("planetName1"), planet("planetName2")), Seq(), Seq())

      "multiple star systems are returned" in {
        val result = setupService(200, jsonResponse = Some(Json.toJson(Seq(system, system2)))).getStarSystem("name", Coordinates(1, 2))

        await(result) shouldBe Some(system)
      }
    }

    "return no star system" when {

      "no star systems are returned" in {
        val result = setupService(200, jsonResponse = Some(Json.toJson(Seq.empty[StarSystem]))).getStarSystem("name", Coordinates(1, 2))

        await(result) shouldBe None
      }
    }

    "return an upstream universe exception" when {

      "a non 200 response is returned" in {
        val result = setupService(500, bodyResponse = Some("error message")).getStarSystem("name", Coordinates(1, 2))

        the[UpstreamUniverseException] thrownBy await(result) shouldBe UpstreamUniverseException(500, "error message")
      }
    }
  }

  "Calling .getStar" should {

    "return a star" when {

      "a star entity is returned first" in {
        val result = setupService(200, jsonResponse = Some(Json.toJson(Seq(star("name"), planet("name"))))).getStar("name", Coordinates(1, 2))

        await(result) shouldBe Some(star("name"))
      }
    }

    "return no star" when {

      "no entities are returned" in {
        val result = setupService(200, jsonResponse = Some(Json.toJson(Seq.empty[Entity]))).getStar("name", Coordinates(1, 2))

        await(result) shouldBe None
      }

      "a non-star entity is returned first" in {
        val result = setupService(200, jsonResponse = Some(Json.toJson(Seq(planet("name"), star("name"))))).getStar("name", Coordinates(1, 2))

        await(result) shouldBe None
      }
    }

    "return an upstream universe exception" when {

      "a non 200 response is returned" in {
        val result = setupService(500, bodyResponse = Some("error message")).getStar("name", Coordinates(1, 2))

        the[UpstreamUniverseException] thrownBy await(result) shouldBe UpstreamUniverseException(500, "error message")
      }
    }
  }
}
