ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "ScalaAsignment"
  )

libraryDependencies++=Seq(
  "org.apache.spark"%"spark-core_2.10"%"1.6.0",
  "org.apache.spark"%"spark-sql_2.10"%"1.6.0"
)

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.3"
)

//testing
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % Test


