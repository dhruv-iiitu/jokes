package com.dhruv.jokes.ui.destinations

sealed class TopLevelDestination(val route: String) {
    data object Home: TopLevelDestination("home_screen")
    data object Bookmarks: TopLevelDestination("bookmarks_screen")
    data object Delete: TopLevelDestination("delete_screen")
}
