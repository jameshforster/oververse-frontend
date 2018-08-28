package views

import models.NodeListModelHelpers._
import org.scalajs.dom.raw.HTMLImageElement
import org.scalajs.dom.{NodeListOf, document}

import scala.scalajs.js.annotation.JSExportTopLevel


object GalaxyMapJs {

  @JSExportTopLevel("selectGalaxyQuadrant")
  def selectGalaxyQuadrant(galaxyName: String, quadrant: Int) = {
    document.querySelectorAll(s"""button[id*="quadrant"]""").asInstanceOf[NodeListOf[HTMLImageElement]].foreach {
      node =>
        if (node.id.contains(quadrant.toString)) {
          node.setAttribute("disabled", "")
          //          node.classList.add("hidden")
        } else {
          node.removeAttribute("disabled")
          //          node.classList.remove("hidden")
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
}
