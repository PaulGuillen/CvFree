package com.devpaul.cvfree

import android.os.Bundle
import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalResources
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.devpaul.core.platform.theme.CvFreeTheme
import com.devpaul.core.platform.theme.SetStatusBarColor
import com.devpaul.core.platform.locale.AppLanguageManager
import com.devpaul.core.platform.locale.AppLanguage
import com.devpaul.cvfree.ui.ForceUpdateScreen
import com.devpaul.cvfree.ui.UpdateState
import com.devpaul.cvfree.ui.openPlayStore
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(AppLanguageManager.wrap(newBase))
    }

    private lateinit var appUpdateManager: AppUpdateManager

    private val immediateUpdateOptions =
        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()

    private var updateState by mutableStateOf<UpdateState>(UpdateState.Loading)
    private var appLanguage by mutableStateOf(AppLanguage.Spanish)

    private val immediateUpdateLauncher =
        registerForActivityResult(StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                showForceUpdateMandatoryMessageAndClose()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appUpdateManager = AppUpdateManagerFactory.create(this)
        appLanguage = AppLanguageManager.current(this)
        enableEdgeToEdge()

        validateAppUpdate()

        setContent {
            val isDarkTheme = isSystemInDarkTheme()
            val statusBarColor = if (isDarkTheme) Color.Black else Color.White
            val navController = rememberNavController()
            val localizedContext = remember(appLanguage) {
                AppLanguageManager.wrap(this@MainActivity, appLanguage)
            }

            SetStatusBarColor(color = statusBarColor, darkIcons = !isDarkTheme)

            key(appLanguage) {
                CompositionLocalProvider(LocalResources provides localizedContext.resources) {
                    CvFreeTheme(darkTheme = isDarkTheme, dynamicColor = false) {
                        Surface(color = MaterialTheme.colorScheme.background) {
                            when (updateState) {
                                UpdateState.Required -> {
                                    ForceUpdateScreen(onOpenStore = ::startImmediateUpdate)
                                }

                                UpdateState.NotRequired -> {
                                    MainGraph(
                                        navController = navController,
                                        appLanguage = appLanguage,
                                        onLanguageSelected = { language ->
                                            AppLanguageManager.set(this@MainActivity, language)
                                            appLanguage = language
                                        },
                                    )
                                }

                                UpdateState.Loading -> Unit
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        validateAppUpdate()
    }

    private fun validateAppUpdate() {
        lifecycleScope.launch {
            updateState = checkForAppUpdate()
        }
    }

    private suspend fun checkForAppUpdate(): UpdateState {
        return try {
            val appUpdateInfo = appUpdateManager.appUpdateInfo.await()

            val updateInProgress =
                appUpdateInfo.updateAvailability() ==
                        UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS

            val hasUpdate =
                appUpdateInfo.updateAvailability() ==
                        UpdateAvailability.UPDATE_AVAILABLE

            val immediateAllowed =
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)

            when {
                updateInProgress -> UpdateState.Required
                hasUpdate && immediateAllowed -> UpdateState.Required
                else -> UpdateState.NotRequired
            }
        } catch (_: Exception) {
            UpdateState.NotRequired
        }
    }

    private fun startImmediateUpdate() {
        lifecycleScope.launch {
            try {
                val appUpdateInfo = appUpdateManager.appUpdateInfo.await()

                val started = appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    immediateUpdateLauncher,
                    immediateUpdateOptions
                )

                if (!started) {
                    openPlayStore(this@MainActivity)
                }
            } catch (_: Exception) {
                openPlayStore(this@MainActivity)
            }
        }
    }

    private fun showForceUpdateMandatoryMessageAndClose() {
        Toast.makeText(
            this,
            AppLanguageManager.wrap(this, appLanguage).getString(R.string.ui_you_must_update_the_app_to_continue_76be00),
            Toast.LENGTH_LONG
        ).show()

        finish()
    }
}
