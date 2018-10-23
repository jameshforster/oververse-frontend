package conf

import play.api.Configuration
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers
import specs.UnitSpec

class AppConfigSpec extends UnitSpec {

  def setupAppConfig: AppConfig = {
    val mockConfiguration = mock[Configuration]

    when(mockConfiguration.get[String](ArgumentMatchers.eq("services.auth.host"))(ArgumentMatchers.any()))
        .thenReturn("http://localhost")

    when(mockConfiguration.get[String](ArgumentMatchers.eq("services.auth.port"))(ArgumentMatchers.any()))
      .thenReturn("9001")

    when(mockConfiguration.get[String](ArgumentMatchers.eq("services.universe.host"))(ArgumentMatchers.any()))
      .thenReturn("http://localhost")

    when(mockConfiguration.get[String](ArgumentMatchers.eq("services.universe.port"))(ArgumentMatchers.any()))
      .thenReturn("9002")

    new AppConfig(mockConfiguration)
  }

  "AppConfig" should {

    "return a valid auth url" in {
      setupAppConfig.authUrl shouldBe "http://localhost:9001"
    }

    "return a valid universe url" in {
      setupAppConfig.universeUrl shouldBe "http://localhost:9002"
    }
  }
}
