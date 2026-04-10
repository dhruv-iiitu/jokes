package com.dhruv.jokes.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.dhruv.jokes.ui.viewmodel.JokesViewModel
import com.dhruv.jokes.utils.DismissButton
import com.dhruv.jokes.utils.addSoundEffect

@Composable
fun DeleteScreen(
    modifier: Modifier,
    viewModel: JokesViewModel = hiltViewModel()
) {
    var showDialog by remember { mutableStateOf(true) }
    val view = LocalView.current
    Column(modifier = modifier) {
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    // Dismiss the dialog when clicked outside
                    showDialog = false
                },
                title = {
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = "Delete Unbookmarked Jokes?",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                text = {
                    Text(
                        text = "This will delete all the unbookmarked jokes from the device.",
                    )
                },
                dismissButton = {
                    DismissButton {
                        addSoundEffect(view)
                        showDialog = false
                    }
                },
                confirmButton = {
                    OutlinedButton(
                        onClick = {
                            // Action when confirm button is clicked
                            addSoundEffect(view)
                            viewModel.deleteUnbookmarkedJokes()
                            showDialog = false
                        }
                    ) {
                        Text("Confirm")
                    }
                }
            )
        }
    }
}