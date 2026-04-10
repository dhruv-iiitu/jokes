package com.dhruv.jokes.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JokeOrSetup(
    @Json(name = "id")
    val id: Int,
    @Json(name = "type")
    val type: String,
    @Json(name = "setup")
    val setup: String?,
    @Json(name = "delivery")
    val punchline: String?,
    @Json(name = "joke")
    val joke: String?
)

@JsonClass(generateAdapter = true)
data class JokesResponse(
    @Json(name = "jokes")
    val jokes: List<JokeOrSetup>
)