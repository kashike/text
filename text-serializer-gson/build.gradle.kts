plugins {
  id("adventure.common-conventions")
}

dependencies {
  api(project(":adventure-api"))
  api("com.google.code.gson:gson:2.8.0")
  testImplementation(project(":adventure-nbt"))
}

applyJarMetadata("net.kyori.adventure.text.serializer.gson")
