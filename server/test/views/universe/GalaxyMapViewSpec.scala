package views.universe

import models.{GalaxyModel, UserDetailsModel}
import org.jsoup.Jsoup
import specs.UnitSpec
import views.html.universe.galaxyMap
import helpers.EntityHelper._
import models.location.Coordinates

class GalaxyMapViewSpec extends UnitSpec {

  "The galaxy map" should {

    "have the correct number of rows and columns" when {

      "provided with a galaxy of size 2" in {
        val galaxy = GalaxyModel("name", 2, true, false)
        val doc = galaxyMap(galaxy, Seq(), UserDetailsModel("name", "email", 100), 0)(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("tr").size() shouldBe 4
        body.select("tr").forEach { row =>
          row.select("td").size() shouldBe 4
        }
      }

      "provided with a galaxy of size 4" in {
        val galaxy = GalaxyModel("name", 4, true, false)
        val doc = galaxyMap(galaxy, Seq(), UserDetailsModel("name", "email", 100), 0)(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("tr").size() shouldBe 6
        body.select("tr").forEach { row =>
          row.select("td").size() shouldBe 6
        }
      }
    }

    "have a table border for the y coordinate" which {

      "sits on the top" when {

        "provided with a quadrant of 0" in {
          val galaxy = GalaxyModel("name", 2, true, false)
          val doc = galaxyMap(galaxy, Seq(), UserDetailsModel("name", "email", 100), 0)(fakeRequest)
          val body = Jsoup.parse(doc.body)

          body.select("tr").first().select("td").forEach { element =>
            element.hasClass("table-border") shouldBe true
          }
        }

        "provided with a quadrant of 1" in {
          val galaxy = GalaxyModel("name", 2, true, false)
          val doc = galaxyMap(galaxy, Seq(), UserDetailsModel("name", "email", 100), 1)(fakeRequest)
          val body = Jsoup.parse(doc.body)

          body.select("tr").first().select("td").forEach { element =>
            element.hasClass("table-border") shouldBe true
          }
        }
      }

      "sits on the bottom" when {

        "provided with a quadrant of 2" in {
          val galaxy = GalaxyModel("name", 2, true, false)
          val doc = galaxyMap(galaxy, Seq(), UserDetailsModel("name", "email", 100), 2)(fakeRequest)
          val body = Jsoup.parse(doc.body)

          body.select("tr").last().select("td").forEach { element =>
            element.hasClass("table-border") shouldBe true
          }
        }

        "provided with a quadrant of 3" in {
          val galaxy = GalaxyModel("name", 2, true, false)
          val doc = galaxyMap(galaxy, Seq(), UserDetailsModel("name", "email", 100), 3)(fakeRequest)
          val body = Jsoup.parse(doc.body)

          body.select("tr").last().select("td").forEach { element =>
            element.hasClass("table-border") shouldBe true
          }
        }
      }
    }

    "have a table border for the x coordinate" which {

      "sits on the left hand side" when {

        "provided with a quadrant of 0" in {
          val galaxy = GalaxyModel("name", 2, true, false)
          val doc = galaxyMap(galaxy, Seq(), UserDetailsModel("name", "email", 100), 0)(fakeRequest)
          val body = Jsoup.parse(doc.body)
          val rows = body.select("tr")

          for (y <- 1 to 3) {
            rows.get(y).select("td").first().hasClass("table-border") shouldBe true
            rows.get(y).select("td").get(1).hasClass("table-border") shouldBe false
          }
        }

        "provided with a quadrant of 2" in {
          val galaxy = GalaxyModel("name", 2, true, false)
          val doc = galaxyMap(galaxy, Seq(), UserDetailsModel("name", "email", 100), 2)(fakeRequest)
          val body = Jsoup.parse(doc.body)
          val rows = body.select("tr")

          for (y <- 0 to 2) {
            rows.get(y).select("td").first().hasClass("table-border") shouldBe true
            rows.get(y).select("td").get(1).hasClass("table-border") shouldBe false
          }
        }
      }

      "sits on the right hand side" when {
        "provided with a quadrant of 1" in {
          val galaxy = GalaxyModel("name", 2, true, false)
          val doc = galaxyMap(galaxy, Seq(), UserDetailsModel("name", "email", 100), 1)(fakeRequest)
          val body = Jsoup.parse(doc.body)
          val rows = body.select("tr")

          for (y <- 1 to 3) {
            rows.get(y).select("td").last().hasClass("table-border") shouldBe true
            rows.get(y).select("td").get(2).hasClass("table-border") shouldBe false
          }
        }

        "provided with a quadrant of 3" in {
          val galaxy = GalaxyModel("name", 2, true, false)
          val doc = galaxyMap(galaxy, Seq(), UserDetailsModel("name", "email", 100), 3)(fakeRequest)
          val body = Jsoup.parse(doc.body)
          val rows = body.select("tr")

          for (y <- 0 to 2) {
            rows.get(y).select("td").last().hasClass("table-border") shouldBe true
            rows.get(y).select("td").get(2).hasClass("table-border") shouldBe false
          }
        }
      }
    }

    "have a the correct list of coordinates" when {

      "provided with a quadrant containing positive coordinates" in {
        val galaxy = GalaxyModel("name", 2, true, false)
        val doc = galaxyMap(galaxy, Seq(), UserDetailsModel("name", "email", 100), 1)(fakeRequest)
        val body = Jsoup.parse(doc.body)

        for (x <- 0 to 2) {
          body.select("tr").first().select("td").get(x).text() shouldBe x.toString
        }
        for (y <- 1 to 3) {
          body.select("tr").get(y).select("td").last().text() shouldBe {3-y}.toString
        }
      }

      "provided with a quadrant containing negative coordinates" in {
        val galaxy = GalaxyModel("name", 2, true, false)
        val doc = galaxyMap(galaxy, Seq(), UserDetailsModel("name", "email", 100), 2)(fakeRequest)
        val body = Jsoup.parse(doc.body)

        for (x <- 1 to 3) {
          body.select("tr").last().select("td").get(x).text() shouldBe {-3 + x}.toString
        }

        for (y <- 0 to 2) {
          body.select("tr").get(y).select("td").first().text() shouldBe {-y}.toString
        }
      }
    }

    "display no stars" when {

      "provided with a galaxy without stars" in {
        val galaxy = GalaxyModel("name", 2, true, false)
        val doc = galaxyMap(galaxy, Seq(), UserDetailsModel("name", "email", 100), 2)(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("img").size() shouldBe 0
      }

      "provided with a galaxy with stars in another quadrant" in {
        val galaxy = GalaxyModel("name", 2, true, false)
        val doc = galaxyMap(galaxy, Seq(testStar), UserDetailsModel("name", "email", 100), 0)(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("img").size() shouldBe 0
      }
    }

    "correctly display stars" when {

      "provided with a single star" in {
        val galaxy = GalaxyModel("name", 2, true, false)
        val doc = galaxyMap(galaxy, Seq(testStar), UserDetailsModel("name", "email", 100), 1)(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("img").size() shouldBe 1
        body.select("img").attr("width") shouldBe "37%"
        body.select("img").attr("src") shouldBe "/versionedAssets/images/redStar.png"
        body.select("img").attr("onClick") shouldBe """parent.selectStarSystem("name", 1, 2)"""
      }

      "provided with multiple stars" in {
        val galaxy = GalaxyModel("name", 5, true, false)
        val doc = galaxyMap(galaxy, Seq(testStar, testStar.copy(coordinates = Coordinates(3, 4))), UserDetailsModel("name", "email", 100), 1)(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("img").size() shouldBe 2
      }
    }
  }
}
