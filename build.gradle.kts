import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

plugins {
  kotlin("multiplatform") apply false
  id("com.android.library") version "8.11.1" apply false
  id("org.jetbrains.kotlin.android") version "2.2.10" apply false
}

plugins.withType<YarnPlugin> {
  the<YarnRootExtension>().apply {
    // Automatically refresh yarn.lock if the Kotlin/JS plugin detects a mismatch
    yarnLockAutoReplace = true
    // Do not fail the build on mismatches; let it update and continue
    yarnLockMismatchReport = YarnLockMismatchReport.WARNING
  }
}
