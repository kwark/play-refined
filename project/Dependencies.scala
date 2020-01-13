import sbt._

object Dependencies {

  val refinedVersion = "0.9.10"
  val play25Version  = "2.5.19"
  val play26Version  = "2.6.23"
  val play27Version  = "2.7.3"

  val refined   =         "eu.timepit"                 %%    "refined"                  % refinedVersion

  val refinedScalacheck = "eu.timepit"                 %%    "refined-scalacheck"       % refinedVersion   % "test"
  val scalaCheck        = "org.scalacheck"             %%    "scalacheck"               % "1.14.2"  % "test"

  val play26WsJson        = "com.typesafe.play"          %%    "play-ws-standalone-json"  % "2.0.7"  % "test"
  val play26Json          = "com.typesafe.play"          %%    "play-json"                % "2.6.14"
  val play26              = "com.typesafe.play"          %%    "play"                     % play26Version
  val play26NettyServer   = "com.typesafe.play"          %%    "play-netty-server"        % play26Version  % "test"
  val scalaTestPlusPlay26 = "org.scalatestplus.play"     %%    "scalatestplus-play"       % "3.1.2"  % "test"

  val play25Json          = "com.typesafe.play"          %%    "play-json"                % play25Version
  val play25              = "com.typesafe.play"          %%    "play"                     % play25Version
  val play25DataCommons   = "com.typesafe.play"          %%    "play-datacommons"         % play25Version
  val play25NettyServer   = "com.typesafe.play"          %%    "play-netty-server"        % play25Version  % "test"
  val scalaTestPlusPlay25 = "org.scalatestplus.play"     %%    "scalatestplus-play"       % "2.0.1"  % "test"
  val play25Ws            = "com.typesafe.play"          %%    "play-ws"                  % play25Version  % "test"
  val play25Test          = "com.typesafe.play"          %%    "play-test"                % play25Version  % "test"

  val play27WsJson        = "com.typesafe.play"          %%    "play-ws-standalone-json"  % "2.0.8"  % "test"
  val play27Json          = "com.typesafe.play"          %%    "play-json"                % "2.7.4"
  val play27              = "com.typesafe.play"          %%    "play"                     % play27Version
  val play27NettyServer   = "com.typesafe.play"          %%    "play-netty-server"        % play27Version  % "test"
  val scalaTestPlusPlay27 = "org.scalatestplus.play"     %%    "scalatestplus-play"       % "4.0.3"  % "test"

  val play27NettyUnix     = "io.netty" % "netty-transport-native-unix-common" % "4.1.45.Final" % "test"

  val testDependencies = Seq(scalaCheck)
  val play27Dependencies = Seq(refined, play27, play27Json, play27NettyServer, scalaTestPlusPlay27, play27WsJson, play27NettyUnix)
  val play26Dependencies = Seq(refined, play26, play26Json, play26NettyServer, scalaTestPlusPlay26, play26WsJson)
  val play25Dependencies = Seq(refined, play25, play25Json, play25DataCommons, play25NettyServer, scalaTestPlusPlay25, play25Test, play25Ws)

}
