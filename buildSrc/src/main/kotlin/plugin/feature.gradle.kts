package plugin

import dependence.*

plugins {
    id("plugin.android.lib")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core-domain"))
    implementation(project(":core-designsystem"))
    implementation(project(":navigation"))
    koinImplementation()
    composeImplementation()
}
