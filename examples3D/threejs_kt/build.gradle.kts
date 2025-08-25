plugins {
  kotlin("multiplatform")
}

repositories {
  mavenCentral()
}

kotlin {
  js(IR) {
    browser()
  }
  sourceSets {
    val jsMain by getting {
      dependencies {
        implementation(npm("three", "0.179.0"))
      }
    }
  }
}