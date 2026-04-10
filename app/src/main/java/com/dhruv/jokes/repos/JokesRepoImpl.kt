package com.dhruv.jokes.repos

import com.dhruv.jokes.data.local.JokesDao
import com.dhruv.jokes.data.local.JokesEntity
import com.dhruv.jokes.data.remote.ApiService
import com.dhruv.jokes.utils.debugLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class JokesRepoImpl @Inject constructor(private val apiService: ApiService,private val jokesDao: JokesDao) : JokesRepo {
    override suspend fun fetchUnbookmarkedJokes(genre: String, amount: Int): Flow<List<JokesEntity>> {
        try {
            val response = apiService.fetchAllJokes(genre = genre, amount = amount)
            val jokesResponse = response.body()

            if (response.isSuccessful && jokesResponse != null) {
                val jokesEntityList = jokesResponse.jokes.map { joke ->
                    JokesEntity(
                        id = joke.id,
                        type = joke.type,
                        setup = joke.setup,
                        punchline = joke.punchline,
                        jokeMessage = joke.joke
                    )
                }
                jokesDao.insertJokesList(jokesEntityList)

            }
        } catch (e: Exception) {
            debugLog(e.message.toString())
        }
        return jokesDao.fetchUnbookmarkedJokes()
    }

    override suspend fun updateBookmarkStatus(id: Int, bookmarked: Boolean) {
        jokesDao.updateBookmarkStatus(jokeId = id, isBookmarked = bookmarked)
    }

    override fun fetchBookmarkedJokes(): Flow<List<JokesEntity>> {
        return jokesDao.fetchBookmarkedJokes()
    }

    override suspend fun deleteUnbookmarkedJokes() {
        jokesDao.deleteUnbookmarkedJokes()
    }

    override suspend fun deleteJokeViaId(id: Int) {
        jokesDao.deleteJokeViaId(id)
    }
}