plugins {
  id("adventure.common-conventions")
}

dependencies {
  api(project(":adventure-api"))
}

applyJarMetadata("net.kyori.adventure.text.serializer.legacy")
