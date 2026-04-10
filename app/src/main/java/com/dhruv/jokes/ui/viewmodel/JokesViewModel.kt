package com.dhruv.jokes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhruv.jokes.repos.JokesRepo
import com.dhruv.jokes.ui.screens.UIstate
import com.dhruv.jokes.utils.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JokesViewModel @Inject constructor(private val jokesRepo: JokesRepo): ViewModel() {

    private val _homeUIstate: MutableStateFlow<UIstate> = MutableStateFlow(UIstate.Initial)
    val homeUiState = _homeUIstate.asStateFlow()

    private val _bookmarkUIstate: MutableStateFlow<UIstate> = MutableStateFlow(UIstate.Initial)
    val bookmarkUiState = _bookmarkUIstate.asStateFlow()

    init{
        debugLog("view model created")
        fetchUnbookmarkedJokes()
    }
    private fun fetchUnbookmarkedJokes(genre: String = "Any", amount: Int = 10) {
        viewModelScope.launch {
            _homeUIstate.value = UIstate.Loading
            try {
               jokesRepo.fetchUnbookmarkedJokes(genre = genre, amount = amount).collect{ jokesList->
                    _homeUIstate.value = UIstate.Success(jokesList)
                }
            }catch (e: Exception){
                _homeUIstate.value = UIstate.Error(e.message.toString())
                debugLog(e.message.toString())
            }
        }
    }

    fun updateBookmarkStatus(id: Int, bookmarked: Boolean) {
        viewModelScope.launch {
            jokesRepo.updateBookmarkStatus(id, bookmarked)
        }
    }

    fun fetchBookmarkedJokes() {
        _bookmarkUIstate.value = UIstate.Loading
        viewModelScope.launch {
                try {
                jokesRepo.fetchBookmarkedJokes().collect{ jokesList->
                    _bookmarkUIstate.value = UIstate.Success(jokesList = jokesList)
                }
            } catch (e: Exception) {
                _bookmarkUIstate.value = UIstate.Error(e.message.toString())
            }
        }
    }

    fun deleteUnbookmarkedJokes() {
        viewModelScope.launch {
            try {
                jokesRepo.deleteUnbookmarkedJokes()
            } catch (e: Exception) {
                debugLog("Error to delete all unbookmarked joke with msg ${e.message}")
            }
        }
    }

    fun deleteJokeViaId(id: Int){
        viewModelScope.launch {
            try {
                jokesRepo.deleteJokeViaId(id = id)
            } catch (e: Exception) {
                debugLog("Error to delete joke with id $id with msg ${e.message}")
            }
        }
    }
}