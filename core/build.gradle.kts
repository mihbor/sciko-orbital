plugins {
  kotlin("multiplatform")
  id("com.android.library")
  id("org.jetbrains.kotlin.android") apply false
  id("maven-publish")
}

group = "ltd.mbor.sciko"
version = "0.1-SNAPSHOT"

repositories {
  mavenCentral()
  google()
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
  js(IR) {
    browser()
  }
  androidTarget {
    publishLibraryVariants("release", "debug")
  }
  sourceSets {
    val commonMain by getting {
      dependencies {
        api("org.jetbrains.kotlinx:multik-core:0.2.3")
        implementation("org.jetbrains.kotlinx:multik-default:0.2.3")
        api("ltd.mbor.sciko:sciko-linalg:0.1-SNAPSHOT")
        api("ltd.mbor.sciko:sciko-analysis:0.1-SNAPSHOT")
      }
    }
  }
}

android {
  compileSdk = 36
  namespace = "ltd.mbor.sciko.orbital"
  defaultConfig {
    minSdk = 21
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }
}

publishing {
  repositories {
    maven {
      name = "GitHubPackages"
      url = uri("https://maven.pkg.github.com/mihbor/sciko-orbital")
      credentials {
        username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
        password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
      }
    }
  }
}
