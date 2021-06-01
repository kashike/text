plugins {
  id("adventure.common-conventions")
}

configurations {
  testCompileOnly {
    extendsFrom(compileOnlyApi.forUseAtConfigurationTime().get())
  }
}

dependencies {
  api("net.kyori:examination-api:1.1.0")
  api("net.kyori:examination-string:1.1.0")
  compileOnlyApi("org.checkerframework:checker-qual:3.13.0")
  compileOnlyApi("org.jetbrains:annotations:21.0.1")
  testImplementation("com.google.guava:guava:23.0")
}

applyJarMetadata("net.kyori.adventure.key")
