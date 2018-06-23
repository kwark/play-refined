import sbt._

object Dependencies {

  val refinedVersion = "0.9.0"
  val play25Version  = "2.5.18"
  val play26Version  = "2.6.15"
  val play27Version  = "2.7.0-M1"

  val refined   =         "eu.timepit"                 %%    "refined"                  % refinedVersion
  val refined_213   =     "eu.timepit"                 %     "refined_2.13.0-M3"        % "0.9.0+82-8b2bcc61"

  val refinedScalacheck = "eu.timepit"                 %%    "refined-scalacheck"       % refinedVersion   % "test"
  val scalaTest         = "org.scalatest"              %%    "scalatest"                % "3.0.5-M1" % "test"
  val scalaCheck        = "org.scalacheck"             %%    "scalacheck"               % "1.14.0"  % "test"

  val play26WsJson        = "com.typesafe.play"          %%    "play-ws-standalone-json"  % "1.1.9"  % "test"
  val play26Json          = "com.typesafe.play"          %%    "play-json"                % "2.6.9"
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

  val play27WsJson        = "com.typesafe.play"          %%    "play-ws-standalone-json"  % "2.0.0-M2"  % "test"
  val play27Json          = "com.typesafe.play"          %%    "play-json"                % "2.6.9"
  val play27              = "com.typesafe.play"          %%    "play"                     % play27Version
  val play27NettyServer   = "com.typesafe.play"          %%    "play-netty-server"        % play27Version  % "test"
  val scalaTestPlusPlay27 = "org.scalatestplus.play"     %%    "scalatestplus-play"       % "4.0.0-M1"  % "test"

  val testDependencies = Seq(scalaTest, scalaCheck)
  val play27Dependencies = Seq(play27, play27Json, play27NettyServer, scalaTestPlusPlay27, play27WsJson)
  val play26Dependencies = Seq(refined, play26, play26Json, play26NettyServer, scalaTestPlusPlay26, play26WsJson)
  val play25Dependencies = Seq(refined, play25, play25Json, play25DataCommons, play25NettyServer, scalaTestPlusPlay25, play25Test, play25Ws)

}
