package views

import models.UserDetailsModel
import org.jsoup.Jsoup
import play.twirl.api.Html
import specs.UnitSpec
import views.html.main

class MainViewSpec extends UnitSpec {

  "The main view" should {

    "display an account and log-out navbar" when {

      "provided with a user details model" in {
        val doc = main("title", Some(UserDetailsModel("name", "email", 50)))(Html("content"))(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("ul.navbar-right li.nav-item a").get(0).text() shouldBe "name"
        body.select("ul.navbar-right li.nav-item a").get(1).text() shouldBe "Log Out"
      }
    }

    "display a register and login navbar" when {

      "not provided with a user details model" in {
        val doc = main("title", None)(Html("content"))(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("ul.navbar-right li.nav-item a").get(0).text() shouldBe "Login"
        body.select("ul.navbar-right li.nav-item a").get(1).text() shouldBe "Register"
      }
    }

    "not have any sidebar links" when {

      "provided with an empty sequence" in {
        val doc = main("title", None)(Html("content"))(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("nav.sidebar").size() shouldBe 0
      }
    }

    "have the correct number of sidebar links" when {

      "provided with a single entry" in {
        val doc = main("title", None, sidebarLinks = Seq(("string1", "string2")))(Html("content"))(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("nav.sidebar").size() shouldBe 1
        body.select("nav.sidebar button").size() shouldBe 1
        body.select("nav.sidebar button").get(0).attr("onClick") shouldBe """displayContent("string1Section")"""
        body.select("nav.sidebar button").get(0).text() shouldBe "string2"
      }

      "provided with multiple entries" in {
        val doc = main("title", None, sidebarLinks = Seq(("string1", "string2"), ("string3", "string4")))(Html("content"))(fakeRequest)
        val body = Jsoup.parse(doc.body)

        body.select("nav.sidebar").size() shouldBe 1
        body.select("nav.sidebar button").size() shouldBe 2
        body.select("nav.sidebar button").get(0).attr("onClick") shouldBe """displayContent("string1Section")"""
        body.select("nav.sidebar button").get(0).text() shouldBe "string2"
        body.select("nav.sidebar button").get(1).attr("onClick") shouldBe """displayContent("string3Section")"""
        body.select("nav.sidebar button").get(1).text() shouldBe "string4"
      }
    }
  }
}
