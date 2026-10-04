plugins { `kotlin-dsl` }

group = "io.github.kamegatze"

version = "unspecified"

repositories { mavenCentral() }

dependencies {
    compileOnly(libs.kotlin.gradle.plugin)
    implementation(libs.testcontainer.mysql)
    implementation(libs.testcontainer.oracle.free)
    implementation(libs.testcontainer.postgresql)
    implementation(libs.mysql)
    implementation(libs.postgresql)
    implementation(libs.ojdbc)
    implementation(gradleApi())
}
