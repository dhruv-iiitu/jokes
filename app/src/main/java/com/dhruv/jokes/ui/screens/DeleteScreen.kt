package com.dhruv.jokes.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.dhruv.jokes.ui.contract.DeleteContract
import com.dhruv.jokes.ui.viewmodel.DeleteViewModel
import com.dhruv.jokes.utils.DismissButton
import com.dhruv.jokes.utils.addSoundEffect
import com.dhruv.jokes.utils.toastMsg

@Composable
fun DeleteScreen(
    modifier: Modifier,
    viewModel: DeleteViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val view = LocalView.current
    val state by viewModel.state.collectAsState()

    // Consume one-time side effects
    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is DeleteContract.SideEffect.ShowToast -> toastMsg(context, effect.message)
            }
        }
    }

    Column(modifier = modifier) {
        if (state.showDialog) {
            AlertDialog(
                onDismissRequest = {
                    viewModel.processIntent(DeleteContract.Intent.DismissDialog)
                },
                title = {
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = "Delete Unbookmarked Jokes?",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                text = {
                    Text(text = "This will delete all the unbookmarked jokes from the device.")
                },
                dismissButton = {
                    DismissButton {
                        addSoundEffect(view)
                        viewModel.processIntent(DeleteContract.Intent.DismissDialog)
                    }
                },
                confirmButton = {
                    OutlinedButton(
                        onClick = {
                            addSoundEffect(view)
                            viewModel.processIntent(DeleteContract.Intent.ConfirmDelete)
                        }
                    ) {
                        Text("Confirm")
                    }
                }
            )
        }
    }
}
