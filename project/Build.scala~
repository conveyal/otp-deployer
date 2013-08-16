import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "otp-deployer"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "commons-io" % "commons-io" % "2.4",
    "org.apache.commons" % "commons-lang3" % "3.1",
    "org.apache.commons" % "commons-exec" % "1.1",
    "org.onebusaway" % "onebusaway-gtfs" % "1.3.2",
    "org.geotools" % "gt-geojson" % "9.4",
    "org.geotools" % "gt-main" % "9.4",
    "org.geotools" % "gt-referencing" % "9.4",
    "com.vividsolutions" % "jts" % "1.10"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
	playAssetsDirectories <+= baseDirectory / "data",
	resolvers += "releases.developer.onebusaway.org" at "http://nexus.onebusaway.org/content/repositories/releases/",
	resolvers += "geotools" at "http://repo.opengeo.org/",
	resolvers += "osgeo" at "http://download.osgeo.org/webdav/geotools/"

  )

}
