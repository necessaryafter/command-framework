plugins {
  kotlin("jvm") version "2.0.21"
  id("io.papermc.paperweight.userdev") version "1.7.4"

  id("java")
  id("maven-publish")
}

group = "harmony.library"
version = "1.2.0"

repositories {
 // mavenLocal()
  mavenCentral()
  maven("https://jitpack.io")
}

dependencies {
  paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT") // Add readable NMS

  compileOnly("it.unimi.dsi:fastutil:8.5.13")
  compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
}

kotlin {
  jvmToolchain(21)
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["kotlin"])
    }
  }
}
