import messages.SharedMessages
import org.scalajs.dom

object ScalaJSExample {

  def main(args: Array[String]): Unit = {
    dom.document.querySelector("#scalajsShoutOut").textContent
  }
}
