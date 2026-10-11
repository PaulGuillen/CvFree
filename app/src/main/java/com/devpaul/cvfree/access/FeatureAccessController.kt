package com.devpaul.cvfree.access

import com.devpaul.cvfree.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

enum class AccessPlan {
    Free,
    Premium,
}

data class FeatureAccessState(
    val plan: AccessPlan,
    val aiResumeImportEnabled: Boolean,
    val premiumPaywallEnabled: Boolean,
    val freeAiScans: Int,
    val premiumMonthlyLimit: Int,
    val aiModelName: String,
)

internal data class FeatureFlags(
    val aiImportEnabled: Boolean = true,
    val premiumPaywallEnabled: Boolean = false,
    val freeAiScans: Int = 3,
    val premiumMonthlyLimit: Int = 30,
    val aiModelName: String = DEFAULT_AI_MODEL,
)

internal interface PremiumEntitlementSource {
    val declaredPlan: AccessPlan
    val hasPremiumAccess: Boolean
}

@Single([PremiumEntitlementSource::class])
internal class BuildVariantPremiumEntitlementSource : PremiumEntitlementSource {
    override val declaredPlan: AccessPlan = when (BuildConfig.ACCESS_PLAN) {
        "premium" -> AccessPlan.Premium
        else -> AccessPlan.Free
    }
    override val hasPremiumAccess: Boolean = BuildConfig.PREMIUM_ACCESS_OVERRIDE
}

@Single
internal class FeatureAccessController(
    private val entitlementSource: PremiumEntitlementSource,
) {
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    private val _state = MutableStateFlow(
        resolveFeatureAccess(entitlementSource, FeatureFlags()),
    )

    val state: StateFlow<FeatureAccessState> = _state.asStateFlow()

    init {
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 3_600
            },
        )
        remoteConfig.setDefaultsAsync(
            mapOf(
                AI_IMPORT_ENABLED to true,
                PREMIUM_PAYWALL_ENABLED to false,
                FREE_AI_SCANS to 3L,
                PREMIUM_MONTHLY_LIMIT to 30L,
                AI_MODEL_NAME to DEFAULT_AI_MODEL,
            ),
        ).addOnCompleteListener { refresh() }
    }

    fun refresh() {
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            _state.value = resolveFeatureAccess(
                entitlementSource = entitlementSource,
                flags = FeatureFlags(
                    aiImportEnabled = remoteConfig.getBoolean(AI_IMPORT_ENABLED),
                    premiumPaywallEnabled = remoteConfig.getBoolean(PREMIUM_PAYWALL_ENABLED),
                    freeAiScans = remoteConfig.getLong(FREE_AI_SCANS).toSafeInt(),
                    premiumMonthlyLimit = remoteConfig.getLong(PREMIUM_MONTHLY_LIMIT).toSafeInt(),
                    aiModelName = remoteConfig.getString(AI_MODEL_NAME)
                        .ifBlank { DEFAULT_AI_MODEL },
                ),
            )
        }
    }
}

internal fun resolveFeatureAccess(
    entitlementSource: PremiumEntitlementSource,
    flags: FeatureFlags,
): FeatureAccessState = FeatureAccessState(
    plan = entitlementSource.declaredPlan,
    aiResumeImportEnabled = flags.aiImportEnabled &&
        (!flags.premiumPaywallEnabled || entitlementSource.hasPremiumAccess),
    premiumPaywallEnabled = flags.premiumPaywallEnabled,
    freeAiScans = flags.freeAiScans.coerceAtLeast(0),
    premiumMonthlyLimit = flags.premiumMonthlyLimit.coerceAtLeast(0),
    aiModelName = flags.aiModelName.ifBlank { DEFAULT_AI_MODEL },
)

private fun Long.toSafeInt(): Int = coerceIn(0, Int.MAX_VALUE.toLong()).toInt()

private const val AI_IMPORT_ENABLED = "ai_import_enabled"
private const val PREMIUM_PAYWALL_ENABLED = "premium_paywall_enabled"
private const val FREE_AI_SCANS = "free_ai_scans"
private const val PREMIUM_MONTHLY_LIMIT = "premium_monthly_limit"
private const val AI_MODEL_NAME = "ai_model_name"
internal const val DEFAULT_AI_MODEL = "gemini-3.8-flash"
