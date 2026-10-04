package dependence

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.ProductFlavor

private const val DIMENSION_ENVIRONMENT = "environment"

fun ApplicationExtension.configureEnvironments() {
    flavorDimensions.add(DIMENSION_ENVIRONMENT)
    productFlavors {
        create("prod") {
            setupProd(this)
            versionNameSuffix = "-prod"
        }
        create("dev") {
            setupDev(this)
            versionNameSuffix = "-dev"
        }
    }
}

fun LibraryExtension.configureEnvironments() {
    flavorDimensions.add(DIMENSION_ENVIRONMENT)
    productFlavors {
        create("prod") {
            setupProd(this)
        }
        create("dev") {
            setupDev(this)
        }
    }
}

private fun setupProd(flavor: ProductFlavor) {
    flavor.dimension = DIMENSION_ENVIRONMENT
    flavor.buildConfigField("String", "VERSION_NAME", "\"1.0.7\"")
    flavor.buildConfigField("String", "ENVIRONMENT", "\"prod\"")
    flavor.buildConfigField("int", "VERSION_CODE", "10")
}

private fun setupDev(flavor: ProductFlavor) {
    flavor.dimension = DIMENSION_ENVIRONMENT
    flavor.buildConfigField("String", "VERSION_NAME", "\"1.0.0\"")
    flavor.buildConfigField("String", "ENVIRONMENT", "\"dev\"")
    flavor.buildConfigField("int", "VERSION_CODE", "1")
}
