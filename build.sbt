name := "cleverbase-assigment"
version := "1.0"
scalaVersion := "2.11.12"
mainClass in Compile := Some("com.cleverbase.MainApp")

val akkaVersion       = "2.3.9"
val sprayVersion      = "1.3.2"

libraryDependencies ++= {
  Seq(
    "com.typesafe.akka"  %% "akka-actor"      % akkaVersion,
    "io.spray"           %% "spray-can"       % sprayVersion,
    "io.spray"           %% "spray-routing"   % sprayVersion,
    "com.typesafe.akka"  %% "akka-slf4j"      % akkaVersion,
    "org.postgresql"     % "postgresql"      % "42.2.8",
    "com.typesafe.slick" %% "slick" % "3.3.0"
  )
}

