package com.dhruv.jokes.ui.contract

import com.dhruv.jokes.data.local.JokesEntity

/**
 * MVI contract for the Jokes (Home) screen.
 *
 * - [State]      : immutable snapshot the UI renders
 * - [Intent]     : user actions / events the screen can emit
 * - [SideEffect] : one-time effects that must not be replayed (toasts, navigation, etc.)
 */
interface JokesContract {

    data class State(
        val isLoading: Boolean = false,
        val jokes: List<JokesEntity> = emptyList(),
        val error: String? = null
    )

    sealed class Intent {
        data object LoadJokes : Intent()
        data class UpdateBookmark(val id: Int, val bookmarked: Boolean) : Intent()
        data class DeleteJoke(val id: Int) : Intent()
    }

    sealed class SideEffect {
        data class ShowToast(val message: String) : SideEffect()
    }
}
