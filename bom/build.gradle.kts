plugins {
  id("java-platform")
  id("adventure.base-conventions")
}

indra {
  configurePublications {
    from(components["javaPlatform"])
  }
}

dependencies {
  constraints {
    sequenceOf(
      "api",
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
      api(project(":adventure-$it"))
    }
  }
}
