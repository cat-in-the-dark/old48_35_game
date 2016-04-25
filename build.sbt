name := "za_bochok_ldjam_35"

version := "2.5.1"

scalaVersion := "2.11.7"

fork in Compile := true

unmanagedResourceDirectories in Compile += file("assets")

val libgdxVersion = "1.9.2"

libraryDependencies ++= Seq(
  "com.badlogicgames.gdx" % "gdx" % libgdxVersion,
  "com.badlogicgames.gdx" % "gdx-backend-lwjgl" % libgdxVersion,
  "com.badlogicgames.gdx" % "gdx-platform" % libgdxVersion classifier "natives-desktop",
  "com.badlogicgames.gdx" % "gdx-freetype" % libgdxVersion,
  "com.badlogicgames.gdx" % "gdx-freetype-platform" % libgdxVersion classifier "natives-desktop",
  "io.socket" % "socket.io-client" % "0.7.0",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.7.2"
)