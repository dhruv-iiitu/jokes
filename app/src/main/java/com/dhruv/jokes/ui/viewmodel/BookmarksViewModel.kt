package com.dhruv.jokes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhruv.jokes.data.local.JokesEntity
import com.dhruv.jokes.repos.JokesRepo
import com.dhruv.jokes.ui.contract.BookmarksContract
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
class BookmarksViewModel @Inject constructor(
    private val jokesRepo: JokesRepo
) : ViewModel() {

    // Single state atom — MVI principle
    private val _state = MutableStateFlow(BookmarksContract.State())
    val state = _state.asStateFlow()

    // One-time side effects delivered via Channel (never replayed)
    private val _sideEffect = Channel<BookmarksContract.SideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    fun processIntent(intent: BookmarksContract.Intent) {
        when (intent) {
            is BookmarksContract.Intent.LoadBookmarks -> loadBookmarks()
            is BookmarksContract.Intent.DeleteJoke -> deleteJoke(intent.id)
            is BookmarksContract.Intent.UpdateBookmark -> updateBookmark(intent.id, intent.bookmarked)
            is BookmarksContract.Intent.ShareJoke -> showShareSheet(intent.joke)
            is BookmarksContract.Intent.DismissShareSheet -> dismissShareSheet()
        }
    }

    // --- Private reducers ---

    private fun loadBookmarks() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                jokesRepo.fetchBookmarkedJokes().collect { jokes ->
                    _state.update { it.copy(isLoading = false, jokes = jokes) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    private fun deleteJoke(id: Int) {
        viewModelScope.launch {
            try {
                jokesRepo.deleteJokeViaId(id)
                _sideEffect.send(BookmarksContract.SideEffect.ShowToast("Joke Deleted"))
            } catch (e: Exception) {
                debugLog("Error deleting joke $id: ${e.message}")
            }
        }
    }

    private fun updateBookmark(id: Int, bookmarked: Boolean) {
        viewModelScope.launch {
            jokesRepo.updateBookmarkStatus(id, bookmarked)
            val message = if (bookmarked) "Joke Bookmarked" else "Joke Unbookmarked"
            _sideEffect.send(BookmarksContract.SideEffect.ShowToast(message))
        }
    }

    private fun showShareSheet(joke: JokesEntity) {
        _state.update { it.copy(jokeToShare = joke, showShareSheet = true) }
    }

    private fun dismissShareSheet() {
        _state.update { it.copy(jokeToShare = null, showShareSheet = false) }
    }
}
