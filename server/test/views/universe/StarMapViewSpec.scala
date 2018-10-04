package views.universe

import models.{StarSystem, UserDetailsModel}
import specs.UnitSpec
import helpers.EntityHelper._
import models.location.Coordinates
import org.jsoup.Jsoup
import views.html.universe.starMap

class StarMapViewSpec extends UnitSpec {

  "The star map" should {

    "generate the map with the correct number of rows and columns" when {

      "provided with any star system" in {
        val starSystem = StarSystem(testStar, Seq(), Seq(), Seq())
        val doc = starMap(starSystem, UserDetailsModel("name", "email", 100))(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("tr").size() shouldBe 17
        body.select("tr").forEach { row =>
          row.select("td").size() shouldBe 17
        }
      }
    }

    "load an image for a star at the origin point" when {

      "provided with any star system" in {
        val starSystem = StarSystem(testStar, Seq(), Seq(), Seq())
        val doc = starMap(starSystem, UserDetailsModel("name", "email", 100))(fakeRequest)
        val body = Jsoup.parse(doc.body)

        val star = body.select("tr").get(8).select("td").get(8).select("img")

        body.select("img").size() shouldBe 1
        star.attr("width") shouldBe "65%"
        star.attr("src") shouldBe "/versionedAssets/images/redStar.png"
        star.attr("onClick") shouldBe """parent.selectStar("galaxyName", 1, 2)"""
      }
    }

    "load an image for any planets" when {

      "provided with only a single planet" in {
        val starSystem = StarSystem(testStar, Seq(testPlanet), Seq(), Seq())
        val doc = starMap(starSystem, UserDetailsModel("name", "email", 100))(fakeRequest)
        val body = Jsoup.parse(doc.body)

        val planet = body.select("tr").get(4).select("td").get(11).select("img")

        body.select("img").size() shouldBe 2
        planet.attr("width") shouldBe "50%"
        planet.attr("src") shouldBe "/versionedAssets/images/planet.png"
        planet.attr("onClick") shouldBe """parent.selectPlanet("galaxyName", 1, 2 ,3, 4)"""
      }

      "provided with multiple planets" in {
        val starSystem = StarSystem(testStar, Seq(testPlanet, testPlanet.copy(orbitalCoordinates = Coordinates(5, -3))), Seq(), Seq())
        val doc = starMap(starSystem, UserDetailsModel("name", "email", 100))(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("img").size() shouldBe 3
      }
    }
  }
}
