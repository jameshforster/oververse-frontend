package views.universe

import models.{GalaxyModel, UserDetailsModel}
import org.jsoup.Jsoup
import specs.UnitSpec
import views.html.universe.universe

class UniverseViewSpec extends UnitSpec {

  "The universe view" should {

    "display a list of galaxies" when {

      "provided with a single galaxy" in {
        val doc = universe(None, Seq(GalaxyModel("name1", 1, false, false)))(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("h2").size() shouldBe 1
        body.select("h2").text() shouldBe "name1"
      }

      "provided with multiple galaxies" in {
        val doc = universe(None, Seq(GalaxyModel("name1", 1, false, false), GalaxyModel("name2", 2, false, false)))(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("h2").size() shouldBe 2
        body.select("h2").get(0).text() shouldBe "name1"
        body.select("h2").get(1).text() shouldBe "name2"
      }
    }

    "display a no galaxies notification" when {

      "provided with no galaxies" in {
        val doc = universe(None, Seq())(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("h2").size() shouldBe 1
        body.select("h2").text() shouldBe "No Active Galaxies"
      }
    }

    "display that the galaxy is inactive" when {

      "provided with an inactive flag" in {
        val doc = universe(None, Seq(GalaxyModel("name1", 1, false, false)))(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("content p").get(1).text() shouldBe "INACTIVE"
      }
    }

    "display that the galaxy is active" when {

      "provided with an active flag" in {
        val doc = universe(None, Seq(GalaxyModel("name1", 1, true, false)))(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("content p").get(1).text() shouldBe "ACTIVE"
      }
    }

    "display that the galaxy is in test mode" when {

      "provided with a galaxy in test mode" in {
        val doc = universe(None, Seq(GalaxyModel("name1", 1, true, true)))(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("content p").size() shouldBe 3
        body.select("content p").get(2).text() shouldBe "TEST MODE"
      }
    }

    "display no message when the galaxy is not in test mode" when {

      "provided with a galaxy in test mode" in {
        val doc = universe(None, Seq(GalaxyModel("name1", 1, true, false)))(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("content p").size() shouldBe 2
      }
    }

    "display a link to the game on the galaxy" when {

      "provided with a galaxy not in test mode" in {
        val doc = universe(None, Seq(GalaxyModel("name1", 1, true, false)))(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("content a").size() shouldBe 1
        body.select("content a").attr("href") shouldBe controllers.routes.GameController.main("name1").url
      }

      "provided in a galaxy in test mode to a moderator" in {
        val doc = universe(Some(UserDetailsModel("name", "email", UserDetailsModel.moderator)), Seq(GalaxyModel("name1", 1, true, true)))(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("content a").size() shouldBe 1
        body.select("content a").attr("href") shouldBe controllers.routes.GameController.main("name1").url
      }
    }

    "display no link to the game on the galaxy" when {

      "provided with a galaxy in test mode to a regular user" in {
        val doc = universe(Some(UserDetailsModel("name", "email", UserDetailsModel.verified)), Seq(GalaxyModel("name1", 1, true, true)))(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("content a").size() shouldBe 0
      }
    }
  }
}
