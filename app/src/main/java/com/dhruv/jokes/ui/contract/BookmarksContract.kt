package com.dhruv.jokes.ui.contract

import com.dhruv.jokes.data.local.JokesEntity

/**
 * MVI contract for the Bookmarks screen.
 *
 * - [State]      : immutable snapshot the UI renders
 * - [Intent]     : user actions / events the screen can emit
 * - [SideEffect] : one-time effects that must not be replayed (toasts, navigation, etc.)
 */
interface BookmarksContract {

    data class State(
        val isLoading: Boolean = false,
        val jokes: List<JokesEntity> = emptyList(),
        val error: String? = null,
        val jokeToShare: JokesEntity? = null,
        val showShareSheet: Boolean = false
    )

    sealed class Intent {
        data object LoadBookmarks : Intent()
        data class DeleteJoke(val id: Int) : Intent()
        data class UpdateBookmark(val id: Int, val bookmarked: Boolean) : Intent()
        data class ShareJoke(val joke: JokesEntity) : Intent()
        data object DismissShareSheet : Intent()
    }

    sealed class SideEffect {
        data class ShowToast(val message: String) : SideEffect()
    }
}
