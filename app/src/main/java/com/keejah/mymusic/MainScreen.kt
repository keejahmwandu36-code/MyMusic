package com.keejah.mymusic

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun MainScreen(
    artists: List<Pair<String, String>>,
    onAddArtist: (String, String) -> Unit,
    onRemoveArtist: (String) -> Unit,
    onSongSelected: (Uri?) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    val navItems = listOf(
        BottomNavItem("Home", Icons.Filled.Home, "home"),
        BottomNavItem("Search", Icons.Filled.Search, "search"),
        BottomNavItem("Library", Icons.Filled.LibraryMusic, "library"),
        BottomNavItem("Download", Icons.Filled.Download, "download")
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0F0F11)
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            indicatorColor = Color(0xFF6C3BFF),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F0F11))
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    artists = artists,
                    onAddArtist = onAddArtist,
                    onRemoveArtist = onRemoveArtist,
                    onSongSelected = { onSongSelected(null) }
                )
                1 -> SearchScreen(onSongSelected = onSongSelected)
                2 -> LibraryScreen(onSongSelected = onSongSelected)
                3 -> DownloadsScreen()
            }
        }
    }
}
