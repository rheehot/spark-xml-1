name := "spark-xml"

version := "0.8.0"

organization := "com.databricks"

scalaVersion := "2.11.12"

spName := "databricks/spark-xml"

crossScalaVersions := Seq("2.11.12", "2.12.10")

scalacOptions := Seq("-unchecked", "-deprecation")

sparkVersion := sys.props.get("spark.testVersion").getOrElse("2.4.4")

sparkComponents := Seq("core", "sql")

// To avoid packaging it, it's Provided below
autoScalaLibrary := false

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "2.6",
  "org.glassfish.jaxb" % "txw2" % "2.3.2",
  "org.slf4j" % "slf4j-api" % "1.7.25" % Provided,
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "com.novocode" % "junit-interface" % "0.11" % Test,
  "org.apache.spark" %% "spark-core" % sparkVersion.value % Test,
  "org.apache.spark" %% "spark-sql" % sparkVersion.value % Test,
  "org.scala-lang" % "scala-library" % scalaVersion.value % Provided
)

// This is necessary because of how we explicitly specify Spark dependencies
// for tests rather than using the sbt-spark-package plugin to provide them.
spIgnoreProvided := true

publishMavenStyle := true

spAppendScalaVersion := true

spIncludeMaven := true

pomExtra :=
  <url>https://github.com/databricks/spark-xml</url>
  <licenses>
    <license>
      <name>Apache License, Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:databricks/spark-xml.git</url>
    <connection>scm:git:git@github.com:databricks/spark-xml.git</connection>
  </scm>
  <developers>
    <developer>
      <id>hyukjinkwon</id>
      <name>Hyukjin Kwon</name>
    </developer>
  </developers>

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

credentials += Credentials(
  "Sonatype Nexus Repository Manager",
  "oss.sonatype.org",
  sys.env.getOrElse("USERNAME", ""),
  sys.env.getOrElse("PASSWORD", ""))

parallelExecution in Test := false

// Skip tests during assembly
test in assembly := {}

// Prints JUnit tests in output
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-v"))

mimaPreviousArtifacts := Set("com.databricks" %% "spark-xml" % "0.7.0")

val ignoredABIProblems = {
  import com.typesafe.tools.mima.core._
  import com.typesafe.tools.mima.core.ProblemFilters._
  Seq(
    exclude[IncompatibleResultTypeProblem](
      "com.databricks.spark.xml.XmlOptions.DEFAULT_NULL_VALUE"),
    exclude[MissingClassProblem]("com.databricks.spark.xml.DefaultSource15"),
    exclude[DirectMissingMethodProblem](
      "com.databricks.spark.xml.util.XmlFile.DEFAULT_ROW_SEPARATOR"),
    exclude[DirectMissingMethodProblem](
      "com.databricks.spark.xml.util.InferSchema.findTightestCommonTypeOfTwo"),
    exclude[DirectMissingMethodProblem]("com.databricks.spark.xml.XmlOptions.dropMalformed"),
    exclude[DirectMissingMethodProblem]("com.databricks.spark.xml.XmlOptions.permissive"),
    exclude[DirectMissingMethodProblem]("com.databricks.spark.xml.XmlOptions.failFast"),
    exclude[MissingClassProblem]("com.databricks.spark.xml.util.ParseModes"),
    exclude[MissingClassProblem]("com.databricks.spark.xml.util.ParseModes$"),
    exclude[MissingTypesProblem]("com.databricks.spark.xml.XmlRelation"),
    exclude[DirectMissingMethodProblem]("com.databricks.spark.xml.XmlRelation.buildScan"),
    exclude[DirectMissingMethodProblem](
      "com.databricks.spark.xml.parsers.StaxXmlParser.com$databricks$" +
        "spark$xml$parsers$StaxXmlParser$$convertObject$default$4"),
    exclude[DirectMissingMethodProblem](
      "com.databricks.spark.xml.util.CompressionCodecs.getCodecClass"),
    exclude[IncompatibleMethTypeProblem](
      "com.databricks.spark.xml.parsers.StaxXmlGenerator.apply")
  )
}

mimaBinaryIssueFilters ++= ignoredABIProblems
