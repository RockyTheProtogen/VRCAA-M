package cc.sovellus.vrcaa.ui.screen.login

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.VRChatApi
import cc.sovellus.vrcaa.extension.twoFactorToken
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.service.PipelineService
import cc.sovellus.vrcaa.ui.screen.navigation.NavigationScreen
import kotlinx.coroutines.launch

class MfaScreenModel(
    private val context: Context,
    private val otpType: VRChatApi.MfaType,
    private val navigator: Navigator
) : ScreenModel {

    private val preferences: SharedPreferences = context.getSharedPreferences("vrcaa_prefs", MODE_PRIVATE)
    var code: MutableState<String> = mutableStateOf("")

    fun verify() {
        screenModelScope.launch {
            val result = api.verifyAccount(otpType, code.value)
            if (result == null) {
                Toast.makeText(
                    context,
                    context.getString(R.string.login_toast_wrong_code),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val intent = Intent(context, PipelineService::class.java)
                context.startService(intent)
                preferences.twoFactorToken = result
                navigator.replace(NavigationScreen())
            }
        }

        code.value = ""
    }
}