rootProject.name = "sciko-orbital"

include(":sciko-orbital")
project(":sciko-orbital").projectDir = file("core")
include(":examples")
include(":examples3D")
include(":examples3D:threejs_kt")
include(":examples3D:three-mesh-ui_kt")

pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
  }
  plugins {
    kotlin("multiplatform") version "2.2.10"
  }
}
plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
