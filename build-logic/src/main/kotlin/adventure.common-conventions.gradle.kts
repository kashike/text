import com.adarshr.gradle.testlogger.theme.ThemeType

plugins {
  id("adventure.base-conventions")
  id("net.kyori.indra")
  id("net.kyori.indra.checkstyle")
  id("net.kyori.indra.license-header")
  id("com.adarshr.test-logger")
  id("info.solidsoft.pitest")
}

pitest {
  junit5PluginVersion.set("0.12")
  // http://pitest.org/quickstart/mutators/
  mutators.set(setOf(
    "CONDITIONALS_BOUNDARY",
    "INCREMENTS",
    "INVERT_NEGS",
    "MATH",
    "NEGATE_CONDITIONALS",
    "VOID_METHOD_CALLS",
    "EMPTY_RETURNS",
    "FALSE_RETURNS",
    "TRUE_RETURNS",
    "PRIMITIVE_RETURNS"
  ))
  targetClasses.set(setOf("net.kyori.adventure.*")) // We don't want to target any other classes on the classpath
  targetTests.set(setOf("net.kyori.*")) // This is used rather than net.kyori.adventure.* because some stuff is under net.kyori.test
  threads.set(Runtime.getRuntime().availableProcessors())
  outputFormats.set(setOf("HTML", "XML"))
  excludedClasses.set(setOf(
    "net.kyori.adventure.audience.ForwardingAudience*", // nothing needs testing here
    // Ignore enums that contain an Index and nothing else that needs to be mutated
    "net.kyori.adventure.bossbar.BossBar.Color",
    "net.kyori.adventure.bossbar.BossBar.Overlay",
    "net.kyori.adventure.bossbar.BossBar.Flag",
    "net.kyori.adventure.sound.Sound.Source",
    "net.kyori.adventure.text.event.ClickEvent.Action",
    "net.kyori.adventure.text.event.HoverEvent.Action"
  ))
  excludedMethods.set(setOf(
    "equals", "hashCode", "toString", // Built-ins we don't care about
    "examinableProperties", "examine", // Examinable stuff can be tested by examination API
    "debuggerString", "detectCycle", "colorIfAbsent", "mapChildren", "mapChildrenDeep", // Impossible to test
    "flatten0" // Absolutely no point testing the stuff it wants us to test for this
  ))
}

testlogger {
  theme = ThemeType.MOCHA_PARALLEL
  showPassed = false
}

configurations.testCompileClasspath {
  exclude(group = "junit") // brought in by google's libs
}

repositories {
  mavenLocal()
  mavenCentral()
}

dependencies {
  api(platform(project(":adventure-bom")))
  checkstyle("ca.stellardrift:stylecheck:0.1")
  testImplementation("com.google.guava:guava-testlib:30.1.1-jre")
  testImplementation("com.google.truth:truth:1.1.3")
  testImplementation("com.google.truth.extensions:truth-java8-extension:1.1.3")
  testImplementation(platform("org.junit:junit-bom:5.7.2"))
  testImplementation("org.junit.jupiter:junit-jupiter-api")
  testImplementation("org.junit.jupiter:junit-jupiter-engine")
  testImplementation("org.junit.jupiter:junit-jupiter-params")
  testImplementation("org.mockito:mockito-core:3.10.0")
}

tasks {
  javadoc {
    val options = options as? StandardJavadocDocletOptions ?: return@javadoc
    options.tags("sinceMinecraft:a:Since Minecraft:")
  }
}
