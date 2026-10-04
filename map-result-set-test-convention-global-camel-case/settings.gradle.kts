rootProject.name = "map-result-set-test-convention-global-camel-case"

dependencyResolutionManagement {
    versionCatalogs { create("libs") { from(files("../gradle/libs.versions.toml")) } }
}
