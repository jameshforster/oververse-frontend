package conf

import play.api.Configuration
import org.mockito.Mockito._
import org.mockito.Matchers
import specs.UnitSpec

class AppConfigSpec extends UnitSpec {

  def setupAppConfig: AppConfig = {
    val mockConfiguration = mock[Configuration]

    when(mockConfiguration.get[String](Matchers.eq("services.auth.host"))(Matchers.any()))
        .thenReturn("http://localhost")

    when(mockConfiguration.get[String](Matchers.eq("services.auth.port"))(Matchers.any()))
      .thenReturn("9001")

    when(mockConfiguration.get[String](Matchers.eq("services.universe.host"))(Matchers.any()))
      .thenReturn("http://localhost")

    when(mockConfiguration.get[String](Matchers.eq("services.universe.port"))(Matchers.any()))
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
