import sbt._

object Dependencies {

  val refinedVersion = "0.11.1"
 
  val play28Version  = "2.8.19"
  val play29Version  = "2.9.1"
  val play30Version  = "3.0.1"

  val refined   =         "eu.timepit"                 %%    "refined"                  % refinedVersion

  val refinedScalacheck = "eu.timepit"                 %%    "refined-scalacheck"       % refinedVersion   % "test"
  val scalaCheck        = "org.scalacheck"             %%    "scalacheck"               % "1.17.0"  % "test"
  val scalaTestPlusChk  = "org.scalatestplus"          %%    "scalacheck-1-17"          % "3.2.17.0" % "test"

  val play30WsJson        = "org.playframework"          %%    "play-ws-standalone-json"  % play30Version  % "test"
  val play30Json          = "org.playframework"          %%    "play-json"                % play30Version
  val play30              = "org.playframework"          %%    "play"                     % play30Version
  val play30NettyServer   = "org.playframework"          %%    "play-netty-server"        % play30Version  % "test"
  val scalaTestPlusPlay30 = "org.scalatestplus.play"     %%    "scalatestplus-play"       % "7.0.0"  % "test"

  val play29WsJson        = "com.typesafe.play"          %%    "play-ws-standalone-json"  % "2.2.5"  % "test"
  val play29Json          = "com.typesafe.play"          %%    "play-json"                % "2.9.4"
  val play29              = "com.typesafe.play"          %%    "play"                     % play29Version
  val play29NettyServer   = "com.typesafe.play"          %%    "play-netty-server"        % play29Version  % "test"
  val scalaTestPlusPlay29 = "org.scalatestplus.play"     %%    "scalatestplus-play"       % "6.0.0"  % "test"

  val play28WsJson        = "com.typesafe.play"          %%    "play-ws-standalone-json"  % "2.1.10"  % "test"
  val play28Json          = "com.typesafe.play"          %%    "play-json"                % "2.9.4"
  val play28              = "com.typesafe.play"          %%    "play"                     % play28Version
  val play28NettyServer   = "com.typesafe.play"          %%    "play-netty-server"        % play28Version  % "test"
  val scalaTestPlusPlay28 = "org.scalatestplus.play"     %%    "scalatestplus-play"       % "5.1.0"  % "test"


  val testDependencies = Seq(scalaCheck, scalaTestPlusChk, refinedScalacheck)
  val play30Dependencies = Seq(refined, play30, play30Json, play30NettyServer, scalaTestPlusPlay30, play30WsJson)
  val play29Dependencies = Seq(refined, play29, play29Json, play29NettyServer, scalaTestPlusPlay29, play29WsJson)
  val play28Dependencies = Seq(refined, play28, play28Json, play28NettyServer, scalaTestPlusPlay28, play28WsJson)
 
}
