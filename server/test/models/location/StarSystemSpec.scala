package models.location

import helpers.EntityHelper
import models.StarSystem
import models.entities.{PlanetEntity, StarEntity}
import specs.UnitSpec

class StarSystemSpec extends UnitSpec {

  "StarSystem" should {
    val star = StarEntity("id", "galaxyName", "name", EntityHelper.validStarAttributes(5, "Red"), Coordinates(0, 0), 100)
    val planet = PlanetEntity("", "", "", EntityHelper.validPlanetAttributes, Coordinates(1, 2), Coordinates(3, 4), 50)

    "return a valid star from stellar object" when {

      "the entity type is correct" in {
        StarSystem(star, Seq(), Seq(), Seq()).star shouldBe star
      }
    }

    "return a list containing only the planets" when {

      "provided with a list of major orbitals with non-planets" in {
        StarSystem(star, Seq(planet, star, planet, planet, star), Seq(), Seq()).planets shouldBe Seq(planet, planet, planet)
      }
    }

    "return an empty list for the planets" when {

      "provided with a list of major orbitals without a planet" in {
        StarSystem(star, Seq(star, star), Seq(), Seq()).planets shouldBe Seq()
      }
    }

    "throw an exception" when {

      "a non-star entity is provided as a stellar entity" in {
        the[Exception] thrownBy {
          StarSystem(planet, Seq(), Seq(), Seq())
        } should have message "models.entities.PlanetEntity cannot be cast to models.entities.StarEntity"
      }
    }
  }
}
