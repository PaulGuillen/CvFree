plugins {
    id("plugin.feature")
}

android {
    namespace = "com.devpaul.features.resume"
}

dependencies {
    implementation(project(":features:templates"))
     implementation(libs.mlkit.text.recognition)
}
