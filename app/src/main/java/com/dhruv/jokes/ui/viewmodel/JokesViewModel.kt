package com.dhruv.jokes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhruv.jokes.repos.JokesRepo
import com.dhruv.jokes.ui.contract.JokesContract
import com.dhruv.jokes.utils.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JokesViewModel @Inject constructor(
    private val jokesRepo: JokesRepo
) : ViewModel() {

    // Single state atom — MVI principle
    private val _state = MutableStateFlow(JokesContract.State())
    val state = _state.asStateFlow()

    // One-time side effects delivered via Channel (never replayed)
    private val _sideEffect = Channel<JokesContract.SideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        processIntent(JokesContract.Intent.LoadJokes)
    }

    fun processIntent(intent: JokesContract.Intent) {
        when (intent) {
            is JokesContract.Intent.LoadJokes -> loadJokes()
            is JokesContract.Intent.UpdateBookmark -> updateBookmark(intent.id, intent.bookmarked)
            is JokesContract.Intent.DeleteJoke -> deleteJoke(intent.id)
        }
    }

    // --- Private reducers ---

    private fun loadJokes() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                jokesRepo.fetchUnbookmarkedJokes(genre = "Any", amount = 10).collect { jokes ->
                    _state.update { it.copy(isLoading = false, jokes = jokes) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
                debugLog(e.message.toString())
            }
        }
    }

    private fun updateBookmark(id: Int, bookmarked: Boolean) {
        viewModelScope.launch {
            jokesRepo.updateBookmarkStatus(id, bookmarked)
            val message = if (bookmarked) "Joke Bookmarked" else "Joke Unbookmarked"
            _sideEffect.send(JokesContract.SideEffect.ShowToast(message))
        }
    }

    private fun deleteJoke(id: Int) {
        viewModelScope.launch {
            try {
                jokesRepo.deleteJokeViaId(id)
                _sideEffect.send(JokesContract.SideEffect.ShowToast("Joke Deleted"))
            } catch (e: Exception) {
                debugLog("Error deleting joke $id: ${e.message}")
            }
        }
    }
}
