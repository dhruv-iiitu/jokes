package com.dhruv.jokes.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.dhruv.jokes.R
import com.dhruv.jokes.ui.destinations.TopLevelDestination
import com.dhruv.jokes.utils.addSoundEffect
import com.dhruv.jokes.utils.toastMsg

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: Int,
    val unselectedIcon: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigation(
    bottomNavItems: List<BottomNavigationItem>
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val navController = rememberNavController()
    val view = LocalView.current

    Scaffold(
        topBar = {
            val context = LocalContext.current
            TopAppBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                title = {
                    Text(text = "Jokes", fontFamily = FontFamily.Cursive, fontSize = 50.sp)
                },
                actions = {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Icon(
                            modifier = Modifier.clickable {
                                addSoundEffect(view)
                                toastMsg(context, "feature will be added soon")
                            },
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "notification"
                        )
                        Icon(
                            modifier = Modifier.clickable {
                                addSoundEffect(view)
                                toastMsg(context, "feature will be added soon")
                            },
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search Icon"
                        )
                    }
                },
                navigationIcon = {
                    Image(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape),
                        painter = painterResource(id = R.drawable.ic_app),
                        contentDescription = "app icon"
                    )
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                bottomNavItems.forEachIndexed { index, bottomItem ->
                    NavigationBarItem(
                        label = { Text(text = bottomItem.title) },
                        selected = index == selectedIndex,
                        onClick = {
                            addSoundEffect(view)
                            navController.popBackStack()
                            selectedIndex = index
                            when (selectedIndex) {
                                1 -> navController.navigate(TopLevelDestination.Bookmarks.route)
                                2 -> navController.navigate(TopLevelDestination.Delete.route)
                                else -> navController.navigate(TopLevelDestination.Home.route)
                            }
                        },
                        icon = {
                            val iconId = if (index == selectedIndex) bottomItem.selectedIcon else bottomItem.unselectedIcon
                            Icon(
                                painter = painterResource(id = iconId),
                                contentDescription = "bottom nav item"
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
