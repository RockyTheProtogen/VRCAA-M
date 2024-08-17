package cc.sovellus.vrcaa.ui.screen.profile

import cafe.adriel.voyager.core.model.StateScreenModel
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.api.vrchat.models.User

sealed class ProfileState {
    data object Init : ProfileState()
    data object Loading : ProfileState()
    data class Result(val profile: User?) : ProfileState()
}

class ProfileScreenModel : StateScreenModel<ProfileState>(ProfileState.Init) {

    private val cacheListener = object : CacheManager.CacheListener {
        override fun profileUpdated() {
            mutableState.value = ProfileState.Loading
            fetchProfile()
        }
    }

    init {
        mutableState.value = ProfileState.Loading
        CacheManager.addListener(cacheListener)
        fetchProfile()
    }

    fun fetchProfile() {
        mutableState.value = ProfileState.Result(CacheManager.getProfile())
    }
}