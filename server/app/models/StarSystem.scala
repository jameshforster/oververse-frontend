package models

import models.entities.{Entity, PlanetEntity, StarEntity}
import play.api.libs.json.{Json, OFormat}

case class StarSystem(stellarObject: Entity, majorOrbitals: Seq[Entity], minorOrbitals: Seq[Entity], otherEntities: Seq[Entity]) {

  val star: StarEntity = stellarObject.asInstanceOf[StarEntity]
  val planets: Seq[PlanetEntity] = majorOrbitals.collect{case planet: PlanetEntity => planet}
}

object StarSystem {
  implicit val formats: OFormat[StarSystem] = Json.format[StarSystem]
}
