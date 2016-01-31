name := "hello"

version := "1.0"

//scalaVersion := "2.10.2"
scalaVersion := "2.11.7"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.0"

libraryDependencies += "com.github.marianobarrios" %% "dregex_2.11" % "0.2-SNAPSHOT"

libraryDependencies += "org.clojure" % "clojure" % "1.6.0"


//resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
//libraryDependencies += "io.github.martintrojer" %% "edn-scala_2.10" % "0.1-SNAPSHOT"

//val scalednVersion = "1.0.0-e8180d08620a607ec47613f8c2585f7784e86625"
//resolvers += bintray.Opts.resolver.mavenRepo("mandubian")
//libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
//libraryDependencies ++= Seq(
//  // only need scaledn parser?
//  "com.mandubian" %% "scaledn-parser"     % scalednVersion
//    // only need scaledn validation/serialization?
//    , "com.mandubian" %% "scaledn-validation" % scalednVersion
//    // only need scaledn macros?
//    , "com.mandubian" %% "scaledn-macros"     % scalednVersion
//)


