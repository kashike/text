plugins {
  id("adventure.common-conventions")
}

dependencies {
  api(project(":adventure-api"))
  api(project(":adventure-text-serializer-gson"))
  api(project(":adventure-nbt"))
}

applyJarMetadata("net.kyori.adventure.text.serializer.gson.legacyimpl")
