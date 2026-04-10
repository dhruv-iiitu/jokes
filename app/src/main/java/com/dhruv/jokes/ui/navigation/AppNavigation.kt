package com.dhruv.jokes.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dhruv.jokes.ui.destinations.TopLevelDestination
import com.dhruv.jokes.ui.screens.BookmarksScreen
import com.dhruv.jokes.ui.screens.DeleteScreen
import com.dhruv.jokes.ui.screens.JokesScreen
import com.dhruv.jokes.ui.viewmodel.JokesViewModel

@Composable
fun AppNavigation(viewModel: JokesViewModel, navController: NavHostController, modifier: Modifier) {
    NavHost(navController = navController, startDestination = TopLevelDestination.Home.route) {
        composable(route = TopLevelDestination.Home.route) {
            JokesScreen(viewModel = viewModel, modifier = modifier.fillMaxSize())
        }
        composable(route = TopLevelDestination.Bookmarks.route){
            BookmarksScreen(viewModel = viewModel, modifier = modifier.fillMaxSize())
        }
        composable(route = TopLevelDestination.Delete.route){
            DeleteScreen(viewModel = viewModel, modifier = modifier.fillMaxSize())
        }
    }
}