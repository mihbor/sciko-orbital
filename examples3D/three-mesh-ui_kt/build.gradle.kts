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
        api(project(":examples3D:threejs_kt"))
        implementation(npm("three-mesh-ui", "6.5.4"))
      }
    }
  }
}
