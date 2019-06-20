import com.typesafe.sbt.SbtPgp.autoImportImpl.pgpSecretRing
import sbt.Keys.publishArtifact
import sbt.file

organization in ThisBuild := "be.venneborg"

scalacOptions in ThisBuild ++= Seq(
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
//  "-deprecation", // warning and location for usages of deprecated APIs
  "-feature", // warning and location for usages of features that should be imported explicitly
  "-unchecked", // additional warnings where generated code depends on assumptions
  //  "-Xlint", // recommended additional warnings
//  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver
//  "-Ywarn-inaccessible",
  "-Ywarn-dead-code",
//  "-Xfatal-warnings",
  "-language:reflectiveCalls",
  "-language:experimental.macros",
  "-Ydelambdafy:method"
)

inThisBuild(List(
  licenses := Seq(("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.txt"))),
  homepage := Some(url(s"https://github.com/kwark/play-refined")),
  scmInfo := Some(ScmInfo(url(s"https://github.com/kwark/play-refined"), "scm:git:git@github.com:kwark/play-refined.git")),
  developers := List(Developer("kwark", "Peter Mortier", "", url("https://github.com/kwark"))),

  pgpPublicRing := file("./travis/local.pubring.asc"),
  pgpSecretRing := file("./travis/local.secring.asc"),

  releaseEarlyWith := BintrayPublisher,
  publishMavenStyle := true,
  publishArtifact in Test := false,
  parallelExecution := false
))

//set source dir to source dir in commonPlayModule
val sourceScalaDir =  (baseDirectory in ThisBuild)( b => Seq( b / "play-refined/src/main/scala"))
val source26ScalaDir =  (baseDirectory in ThisBuild)( b => Seq( b / "play26-refined/src/main/scala"))
val sourceTestDir =   (baseDirectory in ThisBuild)( b => Seq( b / "play-refined/src/test/scala" ))
val source26TestDir =  (baseDirectory in ThisBuild)( b => Seq( b / "play26-refined/src/test/scala"))
val resourceTestDir = (baseDirectory in ThisBuild)( b => Seq( b / "play-refined/src/test/resources" ))

import Dependencies._

lazy val `play27-refined` = project
  .settings(
    name := "play27-refined",
    organization := "be.venneborg"
  )
  .settings(fork in Test := scalaBinaryVersion.value == "2.11") // see https://github.com/sbt/sbt/issues/4609
  .settings(releaseEarlyEnableSyncToMaven := false)
  .settings(unmanagedSourceDirectories in Compile ++= (sourceScalaDir.value ++ source26ScalaDir.value))
  .settings(unmanagedSourceDirectories in Test ++= sourceTestDir.value ++ source26TestDir.value)
  .settings(unmanagedResourceDirectories in Test ++= resourceTestDir.value )
  .settings(libraryDependencies := play27Dependencies ++ testDependencies)
  .settings(crossScalaVersions := (crossScalaVersions in ThisBuild).value)

lazy val `play26-refined` = project
  .settings(
    name := "play26-refined",
    organization := "be.venneborg"
  )
  .settings(releaseEarlyEnableSyncToMaven := false)
  .settings(unmanagedSourceDirectories in Compile ++= sourceScalaDir.value )
  .settings(unmanagedSourceDirectories in Test ++= sourceTestDir.value )
  .settings(unmanagedResourceDirectories in Test ++= resourceTestDir.value )
  .settings(libraryDependencies := play26Dependencies ++ testDependencies)
  .settings(crossScalaVersions := (crossScalaVersions in ThisBuild).value.filter(v => v.startsWith("2.11") || v.startsWith("2.12")))

lazy val `play25-refined` = project
  .settings(
    name := "play25-refined",
    organization := "be.venneborg"
  )
  .settings(releaseEarlyEnableSyncToMaven := false)
  .settings(unmanagedSourceDirectories in Compile ++= sourceScalaDir.value )
  .settings(unmanagedSourceDirectories in Test ++= sourceTestDir.value )
  .settings(unmanagedResourceDirectories in Test ++= resourceTestDir.value )
  .settings(libraryDependencies := play25Dependencies ++ testDependencies)
  .settings(crossScalaVersions := (crossScalaVersions in ThisBuild).value.filter(_ startsWith "2.11"))
  .settings(scalaVersion := (crossScalaVersions in ThisBuild).value.find(_ startsWith "2.11").get)

lazy val example = (project in file("example"))
  .enablePlugins(Play)
  .settings()
  .settings(publishArtifact := false)
  .settings(crossScalaVersions := (crossScalaVersions in ThisBuild).value.filter(_ startsWith "2.12"))
  .settings(scalaVersion := (crossScalaVersions in ThisBuild).value.find(_ startsWith "2.12").get)
  .dependsOn(`play26-refined` % "test->test;compile->test,compile")

lazy val root = (project in file("."))
  .settings(publishArtifact := false)
  .settings(crossScalaVersions := Seq.empty)
  .aggregate(`play27-refined`, `play26-refined`, `play25-refined`, example)
