pluginManagement {
  plugins {
    // Default plugin versions
    val indraVersion = "2.0.5"
    id("net.kyori.indra") version indraVersion
    id("net.kyori.indra.git") version indraVersion
    id("net.kyori.indra.checkstyle") version indraVersion
    id("net.kyori.indra.license-header") version indraVersion
    id("net.kyori.indra.publishing.sonatype") version indraVersion
    id("com.adarshr.test-logger") version "3.0.0"
    id("info.solidsoft.pitest") version "1.6.0"
  }
  includeBuild("build-logic")
}

rootProject.name = "adventure-parent"

// Make sure to update bom/build.gradle.kts when making changes to modules.

sequenceOf(
  "api",
  "bom",
  "extra-kotlin",
  "key",
  "nbt",
  "serializer-configurate3",
  "serializer-configurate4",
  "text-serializer-gson",
  "text-serializer-gson-legacy-impl",
  "text-serializer-legacy",
  "text-serializer-plain"
).forEach {
  include("adventure-$it")
  project(":adventure-$it").projectDir = file(it)
}
