package core

import models.NodeListModelHelpers._
import org.scalajs.dom.raw.HTMLImageElement
import org.scalajs.dom.{NodeListOf, document}

import scala.scalajs.js.annotation.JSExportTopLevel


object MenuJs {

  @JSExportTopLevel("displayContent")
  def displayContent(identifier: String): Unit = {
    document.querySelectorAll(s"""[id*="Section"]""").asInstanceOf[NodeListOf[HTMLImageElement]].foreach {
      node =>
        if (node.id == s"$identifier") {
          node.classList.remove("hidden")
        } else {
          node.classList.add("hidden")
        }
    }
  }
}
