package models

import play.api.libs.json._

trait PlanetType {
  val name: String

}

object PlanetType {
  val extremeTypes: Seq[PlanetType] = Seq(GasGiant, Toxic, Unstable, Radioactive, DeathWorld, Crater)
  val habitableTypes: Seq[PlanetType] = Seq(Island, Jungle, Marsh, Gaia, Garden)
  val mainTypes: Seq[PlanetType] = Seq(Desert, Ocean, Arctic, Frozen, Volcanic, Magma, Mountainous, Cavernous)
  val validTypes: Seq[PlanetType] = extremeTypes ++ habitableTypes ++ mainTypes ++ Seq(Plains)

  private val writes = new Writes[PlanetType] {
    override def writes(o: PlanetType): JsValue = Json.toJson(o.name)
  }

  private val reads = new Reads[PlanetType] {
    override def reads(json: JsValue): JsResult[PlanetType] = json.validate[String].map(stringToType)
  }

  implicit val stringToType: String => PlanetType = key => validTypes.find(_.name == key).getOrElse(Barren)
  implicit val formats: Format[PlanetType] = Format(reads, writes)

  object Barren extends PlanetType {
    override val name: String = "Barren"
  }

  object Crater extends PlanetType {
    override val name: String = "Crater"
  }

  object Toxic extends PlanetType {
    override val name: String = "Toxic"
  }

  object Radioactive extends PlanetType {
    override val name: String = "Radioactive"
  }

  object GasGiant extends PlanetType {
    override val name: String = "Gas Giant"
  }

  object DeathWorld extends PlanetType {
    override val name: String = "Death World"
  }

  object Unstable extends PlanetType {
    override val name: String = "Unstable"
  }

  object Island extends PlanetType {
    override val name: String = "Island"
  }

  object Jungle extends PlanetType {
    override val name: String = "Jungle"
  }

  object Marsh extends PlanetType {
    override val name: String = "Marsh"
  }

  object Garden extends PlanetType {
    override val name: String = "Garden"
  }

  object Gaia extends PlanetType {
    override val name: String = "Gaia"
  }

  object Plains extends PlanetType {
    override val name: String = "Plains"
  }

  object Desert extends PlanetType {
    override val name: String = "Desert"
  }

  object Volcanic extends PlanetType {
    override val name: String = "Volcanic"
  }

  object Magma extends PlanetType {
    override val name: String = "Magma"
  }

  object Arctic extends PlanetType {
    override val name: String = "Arctic"
  }

  object Frozen extends PlanetType {
    override val name: String = "Frozen"
  }

  object Ocean extends PlanetType {
    override val name: String = "Ocean"
  }

  object Mountainous extends PlanetType {
    override val name: String = "Mountainous"
  }

  object Cavernous extends PlanetType {
    override val name: String = "Cavernous"
  }

}