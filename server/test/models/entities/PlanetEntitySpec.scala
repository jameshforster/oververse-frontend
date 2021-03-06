package models.entities

import helpers.EntityHelper
import models.exceptions.InvalidAttributeException
import models.location.Coordinates
import specs.UnitSpec

class PlanetEntitySpec extends UnitSpec {

  "The planet entity" should {

    "not throw an exception" when {

      "provided with valid attributes" in {
        PlanetEntity("randomId", "galaxyName", "planetName", EntityHelper.validPlanetAttributes, Coordinates(1, 4), Coordinates(3, 6), 50) shouldBe an [Entity]
      }
    }

    "throw an InvalidAttributeException" when {

      EntityHelper.validPlanetAttributes.attributes.foreach { x =>

        s"missing the ${x._1} attribute" in {
          the[InvalidAttributeException] thrownBy PlanetEntity(
            "randomId",
            "galaxyName",
            "planetName",
            EntityHelper.validPlanetAttributes.removeAttribute(x._1),
            Coordinates(1, 4),
            Coordinates(3, 6),
            50) should have message s"Attribute: ${x._1} not found!"
        }
      }
    }
  }
}
