import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension
import kotlin.jvm.java

plugins {
  kotlin("multiplatform") apply false
  id("com.android.library") version "8.11.1" apply false
  id("org.jetbrains.kotlin.android") version "2.2.10" apply false
}

subprojects {
  plugins.withType(YarnPlugin::class.java) {
    rootProject.the<YarnRootExtension>().yarnLockAutoReplace = true
  }
}
