rootProject.name = "map-result-set-test-convention-global-snake-case"

dependencyResolutionManagement {
    versionCatalogs { create("libs") { from(files("../gradle/libs.versions.toml")) } }
}
