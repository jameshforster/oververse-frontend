package conf

import com.google.inject._
import play.api.Configuration

@Singleton
class AppConfig @Inject()(configuration: Configuration) {

  val authUrl = s"${configuration.get[String]("services.auth.host")}:${configuration.get[String]("services.auth.port")}"
  val universeUrl = s"${configuration.get[String]("services.universe.host")}:${configuration.get[String]("services.universe.port")}"
}
