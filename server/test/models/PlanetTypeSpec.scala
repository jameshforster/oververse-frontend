package models

import play.api.libs.json.Json
import specs.UnitSpec

class PlanetTypeSpec extends UnitSpec {

  "The PlanetType" should {
    val validJson = Json.parse(""""Plains"""")

    "be read from valid json correctly" in {
      Json.fromJson[PlanetType](validJson).get shouldBe PlanetType.Plains
    }

    "be written to json correctly" in {
      Json.toJson(PlanetType.Plains) shouldBe validJson
    }

    "default to barren if non matching" in {
      Json.fromJson[PlanetType](Json.parse(""""Other"""")).get shouldBe PlanetType.Barren
    }
  }
}
