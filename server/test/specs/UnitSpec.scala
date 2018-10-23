package specs

import java.nio.charset.Charset

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import conf.AppConfig
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import play.api.Configuration
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.http.HeaderNames._
import play.api.http.Status._
import play.api.test.CSRFTokenHelper._

import scala.concurrent.duration.{Duration, SECONDS}
import scala.concurrent.{Await, Future}

trait UnitSpec extends WordSpec with Matchers with MockitoSugar {

  implicit val system: ActorSystem = ActorSystem("QuickStart")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val mockAppConfig: AppConfig = new AppConfig(mock[Configuration]) {
    override val authUrl: String = "http://localhost:9001"
    override val universeUrl: String = "http://localhost:9002"
  }

  val fakeRequest: FakeRequest[_] = FakeRequest()
  val fakeRequestWithToken: FakeRequest[_] = fakeRequest.withSession("authToken" -> "fakeToken")
  val fakeRequestWithTokenAndCSRF: RequestHeader = fakeRequestWithToken.withCSRFToken

  def fakePostRequest(parameters: (String, String)*): Request[AnyContentAsFormUrlEncoded] = fakeRequestWithTokenAndCSRF.withBody(fakeRequest.withFormUrlEncodedBody(parameters:_*).body)

  def await[T](future: Future[T]): T = {
    Await.result(future, Duration.apply(5, SECONDS))
  }

  def statusOf(result: Future[Result]): Int = {
    await(result).header.status
  }

  def redirectLocation(result: Future[Result]): Option[String] = {
    await(result).header match {
      case ResponseHeader(FOUND, headers, _) => headers.get(LOCATION)
      case ResponseHeader(SEE_OTHER, headers, _) => headers.get(LOCATION)
      case ResponseHeader(TEMPORARY_REDIRECT, headers, _) => headers.get(LOCATION)
      case ResponseHeader(MOVED_PERMANENTLY, headers, _) => headers.get(LOCATION)
      case ResponseHeader(_, _, _) => None
    }
  }

  def bodyOf(result: Future[Result])(implicit mat: Materializer): String = {
    val body = await(result).body
    await(body.consumeData).decodeString(Charset.defaultCharset().name())
  }
}
