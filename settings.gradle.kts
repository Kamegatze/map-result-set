rootProject.name = "map-result-set"

includeBuild("map-result-set-app")

pluginManagement {
    includeBuild("test-container-runner-with-test")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

include("map-result-set-test-convention-global-camel-case")

include("map-result-set-test-convention-global-pascal-case")

include("map-result-set-test-convention-global-snake-case")
