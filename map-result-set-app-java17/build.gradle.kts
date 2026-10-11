plugins {
    java
    `java-library`
}

group = "org.kamegatze"

version = "unspecified"

repositories { mavenCentral() }

dependencies {
    api(libs.map.result.set)
}

group = "io.github.kamegatze"

version = "0.0.1-SNAPSHOT"

// Apply a specific Java toolchain to ease working on different environments.
java { toolchain { languageVersion = JavaLanguageVersion.of(17) } }
