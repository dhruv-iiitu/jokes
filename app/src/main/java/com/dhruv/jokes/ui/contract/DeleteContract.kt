package com.dhruv.jokes.ui.contract

/**
 * MVI contract for the Delete screen.
 *
 * - [State]      : immutable snapshot the UI renders
 * - [Intent]     : user actions / events the screen can emit
 * - [SideEffect] : one-time effects that must not be replayed
 */
interface DeleteContract {

    data class State(
        val showDialog: Boolean = true
    )

    sealed class Intent {
        data object ConfirmDelete : Intent()
        data object DismissDialog : Intent()
    }

    sealed class SideEffect {
        data class ShowToast(val message: String) : SideEffect()
    }
}
