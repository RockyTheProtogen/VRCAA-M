package cc.sovellus.vrcaa.ui.screen.settings

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.extension.networkLogging

class SettingsScreenModel : ScreenModel {
    val preferences: SharedPreferences = App.getContext().getSharedPreferences("vrcaa_prefs", MODE_PRIVATE)
    val developerMode = mutableStateOf(preferences.networkLogging)
}