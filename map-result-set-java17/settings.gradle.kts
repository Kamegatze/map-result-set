rootProject.name = "map-result-set-java17"

dependencyResolutionManagement {
    versionCatalogs { create("libs") { from(files("../gradle/map.result.set.versions.toml")) } }
}
