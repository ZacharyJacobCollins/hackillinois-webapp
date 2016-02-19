name          := "login-with-climate"

organization  := "com.climate"

version       := "0.1"

scalaVersion  := "2.11.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "io.spray"            %%  "spray-can"      % sprayV,
    "io.spray"            %%  "spray-httpx"    % sprayV,
    "io.spray"            %%  "spray-routing"  % sprayV,
    "org.scalaj"          %%  "scalaj-http"    % "2.1.0",
    "com.typesafe.akka"   %%  "akka-actor"     % akkaV,
    "org.json4s"          %%  "json4s-native" % "3.3.0"
  )
}
