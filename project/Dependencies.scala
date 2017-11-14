import sbt._

object Dependencies {

  val refinedVersion = "0.8.4"
  val play25Version  = "2.5.16"
  val play26Version  = "2.6.7"

  val refined   =         "eu.timepit"                 %%    "refined"                  % refinedVersion

  val refinedScalacheck = "eu.timepit"                 %%    "refined-scalacheck"       % refinedVersion   % "test"
  val scalaTest =         "org.scalatest"              %%    "scalatest"                % "3.0.3"   % "test"
  val scalaCheck =        "org.scalacheck"             %%    "scalacheck"               % "1.13.5"  % "test"

  val playWsJson          = "com.typesafe.play"          %%    "play-ws-standalone-json"  % "1.1.2"  % "test"

  val play26Json =          "com.typesafe.play"          %%    "play-json"                % play26Version
  val play26 =              "com.typesafe.play"          %%    "play"                     % play26Version
  val play26NettyServer   = "com.typesafe.play"          %%    "play-netty-server"        % play26Version  % "test"
  val scalaTestPlusPlay26 = "org.scalatestplus.play"     %%    "scalatestplus-play"       % "3.1.0"  % "test"

  val play25Json =          "com.typesafe.play"          %%    "play-json"                % play25Version
  val play25 =              "com.typesafe.play"          %%    "play"                     % play25Version
  val play25DataCommons   = "com.typesafe.play"          %%    "play-datacommons"         % play25Version
  val play25NettyServer   = "com.typesafe.play"          %%    "play-netty-server"        % play25Version  % "test"
  val scalaTestPlusPlay25 = "org.scalatestplus.play"     %%    "scalatestplus-play"       % "2.0.1"  % "test"

  val testDependencies = Seq(/*refinedScalacheck, */scalaTest, scalaCheck)
  val play26Dependencies = Seq(refined, play26, play26Json, play26NettyServer, scalaTestPlusPlay26, playWsJson)
  val play25Dependencies = Seq(refined, play25, play25Json, play25DataCommons, play25NettyServer, scalaTestPlusPlay25)

}
