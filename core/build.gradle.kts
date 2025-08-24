plugins {
  kotlin("multiplatform") version "2.0.21"
  id("maven-publish")
}

group = "ltd.mbor.sciko"
version = "0.1-SNAPSHOT"

repositories {
  mavenCentral()
  maven("https://jitpack.io")
}

kotlin {
  sourceSets {
    val commonMain by getting {
      dependencies {
        api("org.jetbrains.kotlinx:multik-core:0.2.3")
        implementation("org.jetbrains.kotlinx:multik-default:0.2.3")
        implementation("com.github.mihbor:sciko-linalg:main-SNAPSHOT")
        implementation("com.github.mihbor:sciko-analysis:main-SNAPSHOT")
      }
    }
  }
  jvm()
  jvmToolchain(21)
}
