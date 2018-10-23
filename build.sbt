import sbtcrossproject.{crossProject, CrossType}

lazy val server = (project in file("server")).settings(commonSettings).settings(
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  pipelineStages := Seq(digest, gzip),
  // triggers scalaJSPipeline when using compile or continuous compilation
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
  libraryDependencies ++= Seq(
    "com.vmunier" %% "scalajs-scripts" % "1.1.2",
    guice,
    ws,
    "org.scalactic" %% "scalactic" % "3.0.5",
    "org.scalatest" %% "scalatest" % "3.0.5" % Test,
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
    "org.jsoup" % "jsoup" % "1.8.3" % Test,
    "org.mockito" % "mockito-core" % "2.23.0" % Test
  ),
  // Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
  EclipseKeys.preTasks := Seq(compile in Compile)
).enablePlugins(PlayScala).
  dependsOn(sharedJvm)

lazy val client = (project in file("client")).settings(commonSettings).settings(
  scalaJSUseMainModuleInitializer := true,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.5",
  ),
  jsDependencies += "org.webjars" % "jquery" % "2.2.1" / "jquery.js" minified "jquery.min.js"
).enablePlugins(ScalaJSPlugin, ScalaJSWeb).
  dependsOn(sharedJs)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(commonSettings).enablePlugins(PlayScala)
lazy val sharedJvm = shared.jvm.settings(
  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play-json" % "2.6.7",
    "org.scalactic" %% "scalactic" % "3.0.5",
    "org.scalatest" %% "scalatest" % "3.0.5" % Test,
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
    "org.mockito" % "mockito-core" % "2.23.0" % Test
  )
)
lazy val sharedJs = shared.js

lazy val commonSettings = Seq(
  scalaVersion := "2.12.5",
  organization := "com.example"
)

// loads the server project at sbt startup
onLoad in Global := (onLoad in Global).value andThen {s: State => "project server" :: s}
