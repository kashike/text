plugins {
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
}

dependencies {
  implementation("net.kyori", "indra-common", "2.0.5")
  implementation("com.adarshr", "gradle-test-logger-plugin", "3.0.0")
  implementation("info.solidsoft.gradle.pitest", "gradle-pitest-plugin", "1.6.0")
}
