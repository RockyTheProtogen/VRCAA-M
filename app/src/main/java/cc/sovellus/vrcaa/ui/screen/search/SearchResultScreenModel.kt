package cc.sovellus.vrcaa.ui.screen.search

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.avatars.models.JustHPartyAvatars
import cc.sovellus.vrcaa.api.avatars.providers.JustHPartyProvider
import cc.sovellus.vrcaa.api.http.models.LimitedUser
import cc.sovellus.vrcaa.api.http.models.Users
import cc.sovellus.vrcaa.api.http.models.World
import cc.sovellus.vrcaa.api.http.models.Worlds
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.launch

class SearchResultScreenModel(
    private val context: Context,
    private val query: String
) : StateScreenModel<SearchResultScreenModel.SearchState>(SearchState.Init) {
    private val avatarProvider = JustHPartyProvider()

    sealed class SearchState {
        data object Init : SearchState()
        data object Loading : SearchState()
        data class Result(
            val worlds: Worlds?,
            val users: Users?,
            val avatars: JustHPartyAvatars?
        ) : SearchState()
    }

    private var worlds: Worlds? = null
    private var users: Users? = null
    private var avatars: JustHPartyAvatars? = null

    var currentIndex = mutableIntStateOf(0)

    init {
        mutableState.value = SearchState.Loading
        getContent()
    }

    private fun getContent() {
        screenModelScope.launch {
            api.getWorlds(
                query = query,
                featured = false,
                n = 50,
                sort = "relevance"
            ).let { worlds = it }

            api.getUsers(
                username = query,
                n = 50
            ).let { users = it }

            avatarProvider.search(
                "search",
                query,
                5000 // Not used
            ).let { avatars = it }

            mutableState.value = SearchState.Result(worlds, users, avatars)
        }
    }
}