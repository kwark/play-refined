import sbt._

object Dependencies {

  val refinedVersion = "0.8.4"

  val refined   =         "eu.timepit"                 %%    "refined"                  % refinedVersion

  val refinedScalacheck = "eu.timepit"                 %%    "refined-scalacheck"       % refinedVersion   % "test"
  val scalaTest =         "org.scalatest"              %%    "scalatest"                % "3.0.3"   % "test"
  val scalaCheck =        "org.scalacheck"             %%    "scalacheck"               % "1.13.5"  % "test"

  val play26Json =          "com.typesafe.play"          %%    "play-json"                % "2.6.6"
  val play26 =              "com.typesafe.play"          %%    "play"                     % "2.6.6"
  val play26NettyServer   = "com.typesafe.play"          %%    "play-netty-server"        % "2.6.6"  % "test"
  val scalaTestPlusPlay26 = "org.scalatestplus.play"     %%    "scalatestplus-play"       % "3.1.0"  % "test"

  val play25Json =          "com.typesafe.play"          %%    "play-json"                % "2.5.15"
  val play25 =              "com.typesafe.play"          %%    "play"                     % "2.5.15"
  val play25NettyServer   = "com.typesafe.play"          %%    "play-netty-server"        % "2.5.15"  % "test"
  val scalaTestPlusPlay25 = "org.scalatestplus.play"     %%    "scalatestplus-play"       % "2.0.1"  % "test"

  val testDependencies = Seq(refinedScalacheck, scalaTest, scalaCheck)
  val play26Dependencies = Seq(refined, play26, play26Json, play26NettyServer, scalaTestPlusPlay26)
  val play25Dependencies = Seq(refined, play25, play25Json, play25NettyServer, scalaTestPlusPlay25)

}
