plugins { java }

group = "org.kamegatze"

version = "unspecified"

repositories { mavenCentral() }

dependencies {
    testImplementation(libs.spring.jdbc)

    testImplementation(libs.map.result.set)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    testImplementation(libs.apache.commons.compress)

    testImplementation(libs.bundles.testcontainer)
    testImplementation(libs.bundles.drivers)
    testImplementation(libs.bundles.flyway)

    testImplementation(libs.logback.classic)

    testRuntimeOnly(libs.junit.platform.launcher)
    testAnnotationProcessor(libs.map.result.set)
    testAnnotationProcessor(libs.logback.classic)
}

tasks.test { useJUnitPlatform() }
