plugins {
  kotlin("multiplatform") version "2.0.21"
  id("maven-publish")
}

group = "ltd.mbor.sciko"
version = "0.1-SNAPSHOT"

repositories {
  mavenCentral()
  maven {
    name = "GitHubPackages"
    url = uri("https://maven.pkg.github.com/mihbor/sciko-linalg")
    credentials {
      username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
      password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
    }
  }
  maven {
    name = "GitHubPackages"
    url = uri("https://maven.pkg.github.com/mihbor/sciko-analysis")
    credentials {
      username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
      password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
    }
  }
}

kotlin {
  jvm()
  jvmToolchain(21)
  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation("org.jetbrains.kotlinx:kandy-lets-plot:0.8.0")
        api(project(":sciko-orbital"))
      }
    }
  }
}
