package com.dhruv.jokes.ui.screens

import android.content.Intent
import android.view.View
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dhruv.jokes.ui.contract.BookmarksContract
import com.dhruv.jokes.ui.viewmodel.BookmarksViewModel
import com.dhruv.jokes.utils.DismissButton
import com.dhruv.jokes.utils.ErrorMessage
import com.dhruv.jokes.utils.LoadIndicator
import com.dhruv.jokes.utils.VerticalSpacer
import com.dhruv.jokes.utils.addSoundEffect
import com.dhruv.jokes.utils.toastMsg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    modifier: Modifier = Modifier,
    viewModel: BookmarksViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val view: View = LocalView.current
    val bottomSheetState = rememberModalBottomSheetState()
    val state by viewModel.state.collectAsState()

    // Load bookmarks when this screen first appears
    LaunchedEffect(Unit) {
        viewModel.processIntent(BookmarksContract.Intent.LoadBookmarks)
    }

    // Consume one-time side effects
    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is BookmarksContract.SideEffect.ShowToast -> toastMsg(context, effect.message)
            }
        }
    }

    when {
        state.isLoading -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LoadIndicator()
            }
        }

        state.error != null -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ErrorMessage(error = state.error!!)
            }
        }

        state.jokes.isEmpty() -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ErrorMessage(error = "You don't have any bookmarks!")
            }
        }

        else -> {
            LazyColumn(modifier = modifier) {
                items(state.jokes, key = { it.id }) { joke ->
                    val dismissState = rememberSwipeToDismissBoxState()
                    LaunchedEffect(dismissState.currentValue) {
                        when (dismissState.currentValue) {
                            SwipeToDismissBoxValue.EndToStart ->
                                viewModel.processIntent(BookmarksContract.Intent.DeleteJoke(joke.id))
                            else -> {}
                        }
                    }
                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            val backgroundColor by animateColorAsState(
                                when (dismissState.targetValue) {
                                    SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.8f)
                                    else -> Color.White
                                }, label = ""
                            )
                            val iconScale by animateFloatAsState(
                                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1.3f else 0.5f,
                                label = ""
                            )
                            Box(
                                Modifier
                                    .padding(16.dp)
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(color = backgroundColor)
                                    .padding(16.dp)
                            ) {
                                Icon(
                                    modifier = Modifier.scale(iconScale).align(Alignment.CenterEnd),
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.White
                                )
                            }
                        }
                    ) {
                        JokeItem(
                            unbookmarkedJoke = joke,
                            jokePressed = { selectedJoke ->
                                viewModel.processIntent(BookmarksContract.Intent.ShareJoke(selectedJoke))
                            }
                        ) { isBookmarked ->
                            viewModel.processIntent(BookmarksContract.Intent.UpdateBookmark(joke.id, isBookmarked))
                        }
                    }
                }
            }
        }
    }

    // Share bottom sheet — driven entirely by BookmarksContract.State
    val shareLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    if (state.showShareSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.processIntent(BookmarksContract.Intent.DismissShareSheet) },
            sheetState = bottomSheetState
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Share it with your Loved ones!",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                VerticalSpacer()
                Row(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DismissButton {
                        addSoundEffect(view)
                        viewModel.processIntent(BookmarksContract.Intent.DismissShareSheet)
                    }
                    OutlinedButton(onClick = {
                        addSoundEffect(view)
                        val jokeText = state.jokeToShare?.let { joke ->
                            if (joke.type == "single") "Joke: ${joke.jokeMessage}"
                            else "Setup: ${joke.setup} \nPunchline: ${joke.punchline}"
                        } ?: ""
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, jokeText)
                        }
                        shareLauncher.launch(Intent.createChooser(sendIntent, "Share joke via..."))
                    }) {
                        Text(text = "Share")
                    }
                }
            }
        }
    }
}
