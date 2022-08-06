rootProject.name = "cyberio"
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        kotlin("jvm") version "1.7.10"
    }
}
include(
    "main",
    "cui",
    "mdt",
    "common",
    "processor",
    "annotations",
)
