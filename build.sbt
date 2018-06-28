name := "trec-pm"

organization := "edu.utdallas.hltri"

version := "1.0-SNAPSHOT"

description := "Text REtrieval Conference (TREC) Precision Medicine Project (2017)"

// enable publishing to maven
publishMavenStyle := true

// do not append scala version to the generated artifacts
crossPaths := false

// do not add scala libraries as a dependency!
autoScalaLibrary := false

// tell SBT to shut up unless its important
logLevel := Level.Warn

fork in run := true

javaOptions in run ++= Seq("-ea", "-Xmx14g", "-server", "-XX:+CMSClassUnloadingEnabled", "-XX:+UseG1GC")

connectInput in run :=   true // Connects stdin to sbt during forked runs

outputStrategy in run :=  Some(StdoutOutput) // Get rid of output prefix

// Faster XML parser implementation
libraryDependencies += "com.fasterxml" % "aalto-xml" % "1.0.0"

// Templating engine (used for HTML output)
libraryDependencies += "org.apache.velocity" % "velocity" % "1.7" exclude("log4j", "log4j") exclude("logkit", "logkit")
libraryDependencies += "org.apache.velocity" % "velocity-tools" % "2.0" exclude("commons-logging", "commons-logging")

// Google's general utility library
libraryDependencies += "com.google.guava" % "guava" % "21.+"
libraryDependencies += "com.google.errorprone" % "error_prone_annotations" % "2.0.15" % "provided"

// Super fast CSV parser
libraryDependencies += "com.univocity" % "univocity-parsers" % "2.4.1"

// Light object graph serialization
libraryDependencies += "io.protostuff" % "protostuff-runtime" % "1.6.0"
libraryDependencies += "io.protostuff" % "protostuff-core" % "1.6.0"

// Retrofit RESTful API helpers
libraryDependencies += "com.squareup.retrofit2" % "retrofit" % "2.3.0"
libraryDependencies += "com.squareup.retrofit2" % "converter-jackson" % "2.3.0"

// Mock web server used for DGIdb unit testing
libraryDependencies += "com.squareup.okhttp3" % "mockwebserver" % "3.8.1" % "test"
libraryDependencies += "com.squareup.retrofit2" % "retrofit-mock" % "2.3.0"

// jUnit testing framework
libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"

// Google's inmemory filesystem implemention (used for integration testing)
libraryDependencies += "com.google.jimfs" % "jimfs" % "1.1" % "test"

lazy val scribe = RootProject(file("../scribe"))

lazy val medbase = RootProject(file("../medbase"))

lazy val util = RootProject(file("../hltri-util"))

lazy val inquire = RootProject(file("../inquire"))

lazy val `trec-pm` = project in file (".") dependsOn(util, scribe, medbase, inquire)
