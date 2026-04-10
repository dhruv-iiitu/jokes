package com.dhruv.jokes.ui.screens

import android.view.View
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dhruv.jokes.R
import com.dhruv.jokes.data.local.JokesEntity
import com.dhruv.jokes.ui.viewmodel.JokesViewModel
import com.dhruv.jokes.utils.CustomRowWith2Values
import com.dhruv.jokes.utils.ErrorMessage
import com.dhruv.jokes.utils.LoadIndicator
import com.dhruv.jokes.utils.addSoundEffect
import com.dhruv.jokes.utils.toastMsg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JokesScreen(
    modifier: Modifier = Modifier,
    viewModel: JokesViewModel = hiltViewModel()
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    when (homeUiState) {
        is UIstate.Loading -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LoadIndicator()
            }
        }

        is UIstate.Error -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val error = (homeUiState as UIstate.Error).message
                ErrorMessage(error = error)
            }
        }

        is UIstate.Success -> {
            val successState = homeUiState as UIstate.Success
            if (successState.jokesList.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ErrorMessage(error = "It seems no joke is stored locally! Check your bookmarked jokes.\nIf nothing works you need to restart the app.")
                }
            } else {
                LazyColumn(modifier = modifier) {
                    items(successState.jokesList, key = { joke ->
                        joke.id
                    }) { joke ->
                        val context = LocalContext.current
                        val dismissState = rememberSwipeToDismissBoxState()
                        LaunchedEffect(key1 = dismissState.currentValue) {
                            when (dismissState.currentValue) {
                                SwipeToDismissBoxValue.EndToStart -> {
                                    toastMsg(context = context, msg = "Joke Deleted")
                                    viewModel.deleteJokeViaId(joke.id)
                                }

                                SwipeToDismissBoxValue.StartToEnd -> {
                                    toastMsg(context = context, msg = "Joke Bookmarked")
                                    viewModel.updateBookmarkStatus(id = joke.id, bookmarked = true)
                                }

                                else -> {}
                            }
                        }
                        SwipeToDismissBox(state = dismissState, backgroundContent = {
                            SwipeToDismissBackgroundContent(dismissState)
                        }
                        ) {
                            JokeItem(unbookmarkedJoke = joke) { isBookmarked ->
                                viewModel.updateBookmarkStatus(joke.id, isBookmarked)
                                toastMsg(context = context, msg = "Joke Bookmarked")
                            }

                        }
                    }
                }
            }
        }

        else -> {}
    }


}

@Composable
fun JokeItem(
    unbookmarkedJoke: JokesEntity,
    jokePressed: (JokesEntity) -> Unit = {},
    updateBookmark: (Boolean) -> Unit
) {
    val view: View = LocalView.current
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable {
                addSoundEffect(view)
                jokePressed(unbookmarkedJoke)
            }
    ) {
        Row(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp)
        ) {
            if (unbookmarkedJoke.type == "single") {
                unbookmarkedJoke.jokeMessage?.let { joke->
                    CustomRowWith2Values(
                        modifier = Modifier.weight(1f),
                        value1 = "Joke",
                        value2 = joke
                    )
                }
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    unbookmarkedJoke.setup?.let { setup ->
                        CustomRowWith2Values(value1 = "Setup", value2 = setup)
                        unbookmarkedJoke.punchline?.let { punchline ->
                            CustomRowWith2Values(value1 = "Punchline", value2 = punchline)
                        }
                    }
                }
            }

            IconToggleButton(checked = unbookmarkedJoke.isBookmarked, onCheckedChange = { value ->
                addSoundEffect(view = view)
                updateBookmark(value)
            }) {
                Icon(
                    painter = if (unbookmarkedJoke.isBookmarked) painterResource(id = R.drawable.baseline_bookmark_added_24) else painterResource(
                        id = R.drawable.outline_bookmark_add_24
                    ), contentDescription = "bookmark"
                )
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissBackgroundContent(dismissState: SwipeToDismissBoxState) {
    // background color
    val backgroundColor by animateColorAsState(
        when (dismissState.targetValue) {
            SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.8f)
            SwipeToDismissBoxValue.StartToEnd -> Color.Green
            else -> Color.White
        }, label = ""
    )
    // icon size
    val iconScale by animateFloatAsState(
        targetValue = if (
            dismissState.targetValue == SwipeToDismissBoxValue.EndToStart ||
            dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd
        ) 1.3f else 0.5f,
        label = ""
    )
    Box(
        Modifier
            .padding(16.dp)
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(color = backgroundColor)
            .padding(16.dp),
    ) {
        if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) {
            Icon(
                modifier = Modifier
                    .scale(iconScale)
                    .align(Alignment.CenterStart),
                painter = painterResource(id = R.drawable.outline_bookmark_add_24),
                contentDescription = "Bookmark",
                tint = Color.White
            )
        } else {
            Icon(
                modifier = Modifier
                    .scale(iconScale)
                    .align(Alignment.CenterEnd),
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete",
                tint = Color.White
            )
        }
    }
}