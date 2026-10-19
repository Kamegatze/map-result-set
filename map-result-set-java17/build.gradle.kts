import org.jreleaser.model.Active
import org.jreleaser.model.Signing

plugins {
    java
    `java-library`
    `maven-publish`
    alias(libs.plugins.org.jreleaser)
}

group = "io.github.kamegatze"

version = libs.versions.map.result.set.version.get()

repositories { mavenCentral() }

dependencies { api(libs.map.result.set.java8) }

// Apply a specific Java toolchain to ease working on different environments.
java {
    withJavadocJar()
    withSourcesJar()
    toolchain { languageVersion = JavaLanguageVersion.of(17) }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = group.toString()
            artifactId = rootProject.name
            version = version.toString()

            from(components["java"])

            pom {
                name.set("Map Result Set")
                description.set(
                    "Mapping object database from ResultSet to custom java object on java 17"
                )
                url.set("https://github.com/Kamegatze/map-result-set")
                inceptionYear.set("2025")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("Kamegatze")
                        name.set("Shiryaev Aleksey")
                        url.set("https://github.com/Kamegatze")
                    }
                }
                scm {
                    url.set("https://github.com/Kamegatze/map-result-set")
                    connection.set("scm:git:git://github.com/Kamegatze/map-result-set.git")
                    developerConnection.set(
                        "scm:git:ssh://git@github.com/Kamegatze/map-result-set.git"
                    )
                }
            }
        }
    }
    repositories { maven { url = uri(layout.buildDirectory.dir("staging-deploy")) } }
}

jreleaser {
    signing {
        active = Active.ALWAYS
        armored = true
        mode = Signing.Mode.MEMORY
    }
    deploy {
        maven {
            mavenCentral {
                val releaseDeploy = create("release-deploy")
                releaseDeploy.active = Active.RELEASE
                releaseDeploy.url = "https://central.sonatype.com/api/v1/publisher"
                releaseDeploy.stagingRepositories = listOf<String>("build/staging-deploy")
            }
            nexus2 {
                val snapshotDeploy = create("snapshot-deploy")
                snapshotDeploy.active = Active.SNAPSHOT
                snapshotDeploy.snapshotUrl =
                    "https://central.sonatype.com/repository/maven-snapshots/"
                snapshotDeploy.applyMavenCentralRules = true
                snapshotDeploy.snapshotSupported = true
                snapshotDeploy.closeRepository = true
                snapshotDeploy.releaseRepository = true
                snapshotDeploy.stagingRepositories = listOf<String>("build/staging-deploy")
            }
        }
    }
}
