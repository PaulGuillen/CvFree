plugins {
    id("plugin.feature")
}

android {
    namespace = "com.devpaul.features.resume"
}

dependencies {
    testImplementation(libs.kotlinx.coroutines.test)
    implementation(project(":features:templates"))
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ai)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.mlkit.text.recognition)
    implementation(libs.mlkit.document.scanner)
}
