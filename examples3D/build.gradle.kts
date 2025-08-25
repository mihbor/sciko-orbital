plugins {
  kotlin("multiplatform")
  id("maven-publish")
}

group = "ltd.mbor.sciko.orbital"
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
}

kotlin {
  js(IR) {
    browser()
    binaries.executable()
  }
  sourceSets {
    val jsMain by getting {
      dependencies {
        implementation(project(":sciko-orbital"))
        implementation("org.jetbrains.kotlinx:multik-core-js:0.2.3")
        implementation("org.jetbrains.kotlinx:multik-kotlin-js:0.2.3")
        api(project("threejs_kt"))
      }
    }
  }
}
