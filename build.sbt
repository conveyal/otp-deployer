import sbt._
import sbt.Keys._
import play.Project._
import java.io.File

play.Project.playJavaSettings

name         := "otp-deployer"

version      := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
    "commons-io" % "commons-io" % "2.4",
    "org.apache.commons" % "commons-lang3" % "3.1",
    "org.apache.commons" % "commons-exec" % "1.1",
    "org.onebusaway" % "onebusaway-gtfs" % "1.3.2",
    "org.geotools" % "gt-geojson" % "9.4",
    "org.geotools" % "gt-main" % "9.4",
    "org.geotools" % "gt-referencing" % "9.4",
    "com.vividsolutions" % "jts" % "1.10"
)

resolvers ++= Seq(
  "releases.developer.onebusaway.org" at "http://nexus.onebusaway.org/content/repositories/releases/",
  "geotools" at "http://repo.opengeo.org/",
  "osgeo" at "http://download.osgeo.org/webdav/geotools/",
  //Resolver.file("Local Repository", file(sys.env.get("PLAY_HOME").map(_ + "/repository/local").getOrElse("")))(Resolver.ivyStylePatterns),
  Resolver.url("play-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns),
  Resolver.url("play-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)
)
