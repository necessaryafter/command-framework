plugins {
  kotlin("jvm") version "2.0.21"
  kotlin("plugin.serialization") version "2.0.21"
  id("java")
  id("maven-publish")
}

group = "harmony.library"
version = "1.0-SNAPSHOT"

repositories {
  mavenLocal()
  mavenCentral()
}

dependencies {
  compileOnly("org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT")
  compileOnly("it.unimi.dsi:fastutil:8.5.13")
}

kotlin {
  jvmToolchain(8)
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["kotlin"])
    }
  }
}
