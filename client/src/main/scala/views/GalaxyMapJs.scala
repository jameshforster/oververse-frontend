package views

import models.NodeListModelHelpers._
import org.scalajs.dom.raw.HTMLImageElement
import org.scalajs.dom.{NodeListOf, document}

import scala.scalajs.js.annotation.JSExportTopLevel


object GalaxyMapJs {

  @JSExportTopLevel("selectGalaxyQuadrant")
  def selectGalaxyQuadrant(galaxyName: String, quadrant: Int): Unit = {
    document.querySelectorAll(s"""button[id*="quadrant"]""").asInstanceOf[NodeListOf[HTMLImageElement]].foreach {
      node =>
        if (node.id.contains(quadrant.toString)) {
          node.setAttribute("disabled", "")
        } else {
          node.removeAttribute("disabled")
        }
    }

    document.querySelector("#mapSection h3 span").textContent = quadrant match {
      case 0 => "Northwest"
      case 1 => "Northeast"
      case 2 => "Southwest"
      case 3 => "Southeast"
      case _ => "Invalid"
    }

    document.querySelector("#mapSection iframe").setAttribute("src", s"/galaxy/$galaxyName?quadrant=$quadrant")
  }

  @JSExportTopLevel("selectStarSystem")
  def selectStarSystem(galaxyName: String, xCoordinate: Int, yCoordinate: Int): Unit = {

    document.querySelectorAll(s"""button[id*="quadrant"]""").asInstanceOf[NodeListOf[HTMLImageElement]].foreach {
      node => node.removeAttribute("disabled")
    }

    document.querySelector("#mapSection h3 span").textContent = s"System (X:$xCoordinate, Y:$yCoordinate)"

    document.querySelector("#mapSection iframe").setAttribute("src", s"/galaxy/$galaxyName/system?x=$xCoordinate&y=$yCoordinate")
  }

  @JSExportTopLevel("selectPlanet")
  def selectPlanet(galaxyName: String, galX: Int, galY: Int, sysX: Int, sysY: Int): Unit = {
    document.querySelector("#mapSection iframe").setAttribute("src", s"/galaxy/$galaxyName/planet?galX=$galX&galY=$galY&sysX=$sysX&sysY=$sysY")
  }
}
