package com.dhruv.jokes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dhruv.jokes.ui.navigation.BottomNavigation
import com.dhruv.jokes.ui.navigation.BottomNavigationItem
import com.dhruv.jokes.ui.theme.JokesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JokesTheme {
                val bottomNavItems = listOf(
                    BottomNavigationItem(
                        title = "Home",
                        selectedIcon = R.drawable.baseline_home_24,
                        unselectedIcon = R.drawable.outline_home_24
                    ),
                    BottomNavigationItem(
                        title = "Bookmarks",
                        selectedIcon = R.drawable.baseline_bookmarks_24,
                        unselectedIcon = R.drawable.outline_bookmarks_24
                    ),
                    BottomNavigationItem(
                        title = "Delete",
                        selectedIcon = R.drawable.baseline_delete_24,
                        unselectedIcon = R.drawable.baseline_delete_outline_24
                    )
                )
                BottomNavigation(bottomNavItems = bottomNavItems)
            }
        }
    }
}
