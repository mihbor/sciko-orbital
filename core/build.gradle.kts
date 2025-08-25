import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension
import kotlin.jvm.java

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
}

kotlin {
  jvm()
  jvmToolchain(21)
  js(IR) {
    browser()
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

rootProject.plugins.withType(YarnPlugin::class.java) {
  rootProject.the<YarnRootExtension>().yarnLockAutoReplace = true
}
