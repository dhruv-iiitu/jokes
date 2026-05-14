package com.dhruv.jokes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhruv.jokes.repos.JokesRepo
import com.dhruv.jokes.ui.contract.DeleteContract
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
class DeleteViewModel @Inject constructor(
    private val jokesRepo: JokesRepo
) : ViewModel() {

    // Single state atom — MVI principle
    private val _state = MutableStateFlow(DeleteContract.State())
    val state = _state.asStateFlow()

    // One-time side effects delivered via Channel (never replayed)
    private val _sideEffect = Channel<DeleteContract.SideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    fun processIntent(intent: DeleteContract.Intent) {
        when (intent) {
            is DeleteContract.Intent.ConfirmDelete -> deleteUnbookmarked()
            is DeleteContract.Intent.DismissDialog -> dismissDialog()
        }
    }

    // --- Private reducers ---

    private fun deleteUnbookmarked() {
        viewModelScope.launch {
            try {
                jokesRepo.deleteUnbookmarkedJokes()
                _sideEffect.send(DeleteContract.SideEffect.ShowToast("Unbookmarked jokes deleted"))
            } catch (e: Exception) {
                debugLog("Error deleting unbookmarked jokes: ${e.message}")
                _sideEffect.send(DeleteContract.SideEffect.ShowToast("Failed to delete jokes"))
            } finally {
                _state.update { it.copy(showDialog = false) }
            }
        }
    }

    private fun dismissDialog() {
        _state.update { it.copy(showDialog = false) }
    }
}
