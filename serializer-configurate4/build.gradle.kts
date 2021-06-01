plugins {
  id("adventure.common-conventions")
}

dependencies {
  api(project(":adventure-api"))
  api("org.spongepowered:configurate-core:4.0.0")
  testImplementation(project(":adventure-text-serializer-gson"))
}

applyJarMetadata("net.kyori.adventure.serializer.configurate4")
