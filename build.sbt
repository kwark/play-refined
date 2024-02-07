import sbt.Keys.publishArtifact
import sbt.file

ThisBuild / scalacOptions ++= Seq(
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

Global / concurrentRestrictions += Tags.limit(Tags.Compile, 1)

ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always

inThisBuild(List(
  licenses := Seq(("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.txt"))),
  homepage := Some(url(s"https://github.com/kwark/play-refined")),
  scmInfo := Some(ScmInfo(url(s"https://github.com/kwark/play-refined"), "scm:git:git@github.com:kwark/play-refined.git")),
  developers := List(Developer("kwark", "Peter Mortier", "", url("https://github.com/kwark"))),
  organization := "be.venneborg",
  Test / publishArtifact := false,
  parallelExecution := false,
  scalaVersion := "2.13.8"

))

//set source dir to source dir in commonPlayModule
val commonSourceScalaDir =  (ThisBuild / baseDirectory )( b => Seq( b / "play-refined/src/main/scala"))
val commonSourceTestDir =   (ThisBuild / baseDirectory )( b => Seq( b / "play-refined/src/test/scala" ))
val play28SourceTestDir =   (ThisBuild / baseDirectory )( b => Seq( b / "play28-refined/src/test/scala" ))
val play29SourceTestDir =   (ThisBuild / baseDirectory )( b => Seq( b / "play29-refined/src/test/scala" ))
val play30SourceTestDir =   (ThisBuild / baseDirectory )( b => Seq( b / "play30-refined/src/test/scala" ))
val resourceTestDir = (ThisBuild / baseDirectory )( b => Seq( b / "play-refined/src/test/resources" ))

import Dependencies._


lazy val `play30-refined` = project
  .settings(
    name := "play30-refined",
    organization := "be.venneborg"
  )
  .settings(Compile / unmanagedSourceDirectories ++= commonSourceScalaDir.value )
  .settings(Test / unmanagedSourceDirectories ++= commonSourceTestDir.value ++ play30SourceTestDir.value)
  .settings(Test / unmanagedResourceDirectories ++= resourceTestDir.value )
  .settings(libraryDependencies := play30Dependencies ++ testDependencies)

lazy val `play29-refined` = project
  .settings(
    name := "play29-refined",
    organization := "be.venneborg"
  )
  .settings(Compile / unmanagedSourceDirectories ++= commonSourceScalaDir.value )
  .settings(Test / unmanagedSourceDirectories ++= commonSourceTestDir.value ++ play29SourceTestDir.value)
  .settings(Test / unmanagedResourceDirectories ++= resourceTestDir.value )
  .settings(libraryDependencies := play29Dependencies ++ testDependencies)


lazy val `play28-refined` = project
  .settings(
    name := "play28-refined",
    organization := "be.venneborg"
  )
  .settings(Compile / unmanagedSourceDirectories ++= commonSourceScalaDir.value )
  .settings(Test / unmanagedSourceDirectories ++= commonSourceTestDir.value ++ play28SourceTestDir.value)
  .settings(Test / unmanagedResourceDirectories ++= resourceTestDir.value )
  .settings(libraryDependencies := play28Dependencies ++ testDependencies)


lazy val example = (project in file("example"))
  .enablePlugins(PlayWeb)
  .settings()
  .settings(publishArtifact := false)
  .dependsOn(`play29-refined` % "test->test;compile->test,compile")

lazy val `play-refined` = (project in file("."))
  .settings(publishArtifact := false)
  .aggregate(`play30-refined`, `play29-refined`, `play28-refined`, `example`)
