plugins {
  id("adventure.common-conventions")
  kotlin("jvm") version "1.4.32"
}

tasks {
  sequenceOf(compileKotlin, compileTestKotlin).forEach {
    it.configure {
      kotlinOptions {
        jvmTarget = "1.8"
      }
    }
  }
}

dependencies {
  api(project(":adventure-api"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

applyJarMetadata("net.kyori.adventure.extra.kotlin")
