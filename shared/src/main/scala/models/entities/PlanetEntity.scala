package models.entities

import models.PlanetType
import models.attributes.Attributes
import models.location.Coordinates

case class PlanetEntity(id: String, galaxyName: String, name: String, attributes: Attributes, galacticCoordinates: Coordinates, orbitalCoordinates: Coordinates, signature: BigDecimal) extends OrbitalEntity {
  override val entityType: String = Entity.planet

  val size: Int = attributes.getOrException[Int](Attributes.size)
  val atmosphere: Int = attributes.getOrException[Int](Attributes.atmosphere)
  val geology: Int = attributes.getOrException[Int](Attributes.geology)
  val biosphere: Int = attributes.getOrException[Int](Attributes.biosphere)
  val toxicity: Int = attributes.getOrException[Int](Attributes.toxicity)
  val radioactivity: Int = attributes.getOrException[Int](Attributes.radioactivity)
  val minerals: Int = attributes.getOrException[Int](Attributes.minerals)
  val danger: Int = attributes.getOrException[Int](Attributes.danger)
  val breathable: Boolean = attributes.getOrException[Int](Attributes.breathable) > 1
  val water: Int = attributes.getOrException[Int](Attributes.water)
  val temperature: Int = attributes.getOrException[Int](Attributes.temperature)
  val planetType: PlanetType = attributes.getOrException[String](Attributes.planetType)
  val children: Option[List[String]] = attributes.getAttribute[List[String]](Attributes.children)

  val atmosphereDescription: String = {
    atmosphere match {
      case 0 => "None"
      case 1 | 2 => "Sparse"
      case 3 | 4 => "Comfortable"
      case 5 | 6 => "Dense"
    }
  }

  val geologyDescription: String = {
    geology match {
      case 0 => "Inactive"
      case 1 | 2 => "Low"
      case 3 | 4 => "High"
      case 5 | 6 => "Unstable"
    }
  }

  val toxicityDescription: String = {
    toxicity match {
      case 0 => "None"
      case 1 | 2 => "Low"
      case 3 | 4 => "High"
      case 5 | 6 => "Dangerously High"
    }
  }

  val temperatureDescription: String = {
    temperature match {
      case 0 | 1 => "Dangerously Low"
      case 2 | 3 | 4 => "Safe"
      case 5 | 6 => "Dangerously High"
    }
  }

  val radioactivityDescription: String = {
    radioactivity match {
      case 0 => "None"
      case 1 | 2 => "Low"
      case 3 | 4 => "High"
      case 5 | 6 => "Dangerously High"
    }
  }

  val mineralsDescription: String = {
    minerals match {
      case 0 => "None"
      case 1 | 2 => "Low"
      case 3 | 4 => "High"
      case 5 | 6 => "Abundant"
    }
  }

  val waterDescription: String = {
    water match {
      case 0 => "None"
      case 1 => "Sparse"
      case 2 | 3 | 4 => "Liveable"
      case 5 => "Abundant"
      case 6 => "Oceanic"
    }
  }

  val biosphereDescription: String = {
    biosphere match {
      case 0 => "None"
      case 1 | 2 => "Some"
      case 3 | 4 => "Diverse"
      case 5 | 6 => "Abundant"
    }
  }

  val dangerDescription: String = {
    danger match {
      case 0 => "Safe"
      case 1 | 2 => "Minor Threat"
      case 3 | 4 => "Major Threat"
      case 5 | 6 => "Extreme Threat"
    }
  }

}
