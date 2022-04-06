name := """sms-service"""
maintainer := "fast.daemon@gmail.com"
version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava).settings(
  watchSources ++= (baseDirectory.value / "public/ui" ** "*").get,
//  Test / test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v")),
//  Compile / scalacOptions += "-deprecation",
//  Compile / console / scalacOptions += "-Ywarn-numeric-widen",
//  Compile / unmanagedClasspath += baseDirectory.value /  "*.java",
//  Compile / unmanagedClasspath += baseDirectory.value /  "*.html",
//  Compile / unmanagedResourceDirectories += baseDirectory( _ / "app" ).value
)

scalaVersion := "2.12.12"

resolvers += Resolver.bintrayRepo("micronautics", "play")

libraryDependencies += guice
libraryDependencies += javaJdbc
libraryDependencies += javaWs

// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test

libraryDependencies += "com.ticketfly" %% "play-liquibase" % "2.1"
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.6"

libraryDependencies += "org.mybatis" % "mybatis" % "3.5.1"
libraryDependencies += "org.mybatis" % "mybatis-guice" % "3.10"

libraryDependencies += "com.google.inject.extensions" % "guice-multibindings" % "4.2.2"
libraryDependencies += "com.google.inject" % "guice" % "4.2.2"
libraryDependencies += "javax.inject" % "javax.inject" % "1"
libraryDependencies += "com.squareup.okhttp3" % "okhttp" % "3.2.0"
libraryDependencies += "com.stripe" % "stripe-java" % "5.8.0"
libraryDependencies += "org.apache.commons" % "commons-text" % "1.8"

libraryDependencies += "com.twilio.sdk" % "twilio" % "8.25.0"

//libraryDependencies +=  "com.micronautics" %% "play-access-logger" % "1.2.2" withSources()

// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

// Add app folder as resource directory so that mapper xml files are in the classpath
unmanagedResourceDirectories in Compile += baseDirectory( _ / "app" ).value

// but filter out java and html files that would then also be copied to the classpath
excludeFilter in Compile in unmanagedResources := "*.java" || "*.html"

scriptClasspath += "conf/*"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.7.1"

//libraryDependencies += "com.typesafe.play" %% "play-mailer" % "2.7.1"
libraryDependencies += "com.typesafe.play" %% "play-iteratees" % "2.6.1"


resolvers += Resolver.bintrayRepo("playframework", "maven")
resolvers += Resolver.bintrayRepo("playframework", "sbt-plugin-releases")

//PlayKeys.devSettings += "play.server.http.idleTimeout" -> "infinite"

