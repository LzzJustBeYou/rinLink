pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "RinLink"
include(":app")
include(":core")
include(":modules:lan")
include(":modules:zigbee")
include(":modules:websocket")
include(":modules:ble")
include(":modules:mqtt")
