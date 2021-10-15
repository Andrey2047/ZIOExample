ThisBuild / scalaVersion     := "2.12.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "ZIOExamples",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "2.0.0-M3",
      "dev.zio" %% "zio-streams" % "2.0.0-M3",
      "dev.zio" %% "zio-test" % "2.0.0-M3" % Test
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
