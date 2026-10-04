plugins {
    id("plugin.application")
}

android {
    namespace = "com.devpaul.cvfree"

    defaultConfig {
        applicationId = "com.devpaul.cvfree"
    }
}

dependencies {
    implementation(project(":navigation"))
    implementation(project(":core-platform"))
    implementation(project(":core-designsystem"))
    implementation(project(":core-data"))
    implementation(project(":core-domain"))

    // Features
    implementation(project(":features:splash"))
    implementation(project(":features:auth"))
    implementation(project(":features:home"))
    implementation(project(":features:onboarding"))
    implementation(project(":features:resume"))
    implementation(project(":features:settings"))
    implementation(project(":features:templates"))

    implementation(libs.androidx.activity.ktx)
    implementation(libs.app.update.ktx)
}
