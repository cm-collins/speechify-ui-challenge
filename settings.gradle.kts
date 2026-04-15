@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

// The Foojay toolchain resolver plugin is convenient in local dev environments because it can
// auto-download JDKs. However, with Gradle 9.x this particular version can fail during
// configuration (e.g. missing vendor constants like `JvmVendorSpec.IBM_SEMERU`).
//
// This project already builds fine with the system JDK (we use Java 21 for the build/AGP and
// Kotlin compiles with toolchain 17 as configured in app/build.gradle.kts), so we disable it.

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Compose UI Challenge"
include(":app")
 
