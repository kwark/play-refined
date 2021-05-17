import sbt._

object Dependencies {

  val refinedVersion = "0.9.24"
  val play27Version  = "2.7.9"
  val play28Version  = "2.8.8"

  val refined   =         "eu.timepit"                 %%    "refined"                  % refinedVersion

  val refinedScalacheck = "eu.timepit"                 %%    "refined-scalacheck"       % refinedVersion   % "test"
  val scalaCheck        = "org.scalacheck"             %%    "scalacheck"               % "1.15.4"  % "test"

  val play28WsJson        = "com.typesafe.play"          %%    "play-ws-standalone-json"  % "2.1.3"  % "test"
  val play28Json          = "com.typesafe.play"          %%    "play-json"                % "2.9.1"
  val play28              = "com.typesafe.play"          %%    "play"                     % play28Version
  val play28NettyServer   = "com.typesafe.play"          %%    "play-netty-server"        % play28Version  % "test"
  val scalaTestPlusPlay28 = "org.scalatestplus.play"     %%    "scalatestplus-play"       % "4.0.3"  % "test"

  val play27WsJson        = "com.typesafe.play"          %%    "play-ws-standalone-json"  % "2.0.8"  % "test"
  val play27Json          = "com.typesafe.play"          %%    "play-json"                % "2.7.4"
  val play27              = "com.typesafe.play"          %%    "play"                     % play27Version
  val play27NettyServer   = "com.typesafe.play"          %%    "play-netty-server"        % play27Version  % "test"
  val scalaTestPlusPlay27 = "org.scalatestplus.play"     %%    "scalatestplus-play"       % "4.0.3"  % "test"
  val play27NettyUnix     = "io.netty" % "netty-transport-native-unix-common" % "4.1.64.Final" % "test"

  val testDependencies = Seq(scalaCheck)
  val play28Dependencies = Seq(refined, play28, play28Json, play28NettyServer, scalaTestPlusPlay28, play28WsJson)
  val play27Dependencies = Seq(refined, play27, play27Json, play27NettyServer, scalaTestPlusPlay27, play27WsJson, play27NettyUnix)

}
