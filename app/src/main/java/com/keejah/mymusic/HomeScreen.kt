package com.keejah.mymusic

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    artists: List<Pair<String, String>>,
    onAddArtist: (String, String) -> Unit,
    onRemoveArtist: (String) -> Unit,
    onSongSelected: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newArtistName by remember { mutableStateOf("") }
    var newArtistUrl by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F11))
            .padding(16.dp)
    ) {
        Text(
            text = "Welcome to MarzVibe",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "Unlimited of Music",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            FilterChipItem(text = "All", isSelected = true)
            FilterChipItem(text = "Music", isSelected = false)
            FilterChipItem(text = "Podcasts", isSelected = false)
        }

        // Favourite Artists Header with Add Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Favourite Artists",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Artist", tint = Color.White)
            }
        }

        // Artist List
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(bottom = 24.dp)
                .height(110.dp) // Fixed height to prevent layout jumps
        ) {
            items(artists) { artist ->
                ArtistItem(
                    name = artist.first,
                    imageUrl = artist.second,
                    onLongClick = { onRemoveArtist(artist.first) }
                )
            }
        }

        Text(
            text = "My Playlist",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val playlists = listOf(
            "Arlene Popul" to "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400",
            "Sad Vibes" to "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400",
            "Future Perfect" to "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400"
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(playlists) { playlist ->
                PlaylistItem(
                    name = playlist.first,
                    imageUrl = playlist.second,
                    onClick = onSongSelected
                )
            }
        }
    }

    // Add Artist Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New Artist", color = Color.White) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newArtistName,
                        onValueChange = { newArtistName = it },
                        label = { Text("Artist Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF6C3BFF)
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newArtistUrl,
                        onValueChange = { newArtistUrl = it },
                        label = { Text("Image URL (optional)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF6C3BFF)
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tip: Long-press an artist to remove them.", color = Color.Gray, fontSize = 12.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onAddArtist(newArtistName, newArtistUrl)
                    newArtistName = ""
                    newArtistUrl = ""
                    showAddDialog = false
                }) {
                    Text("Add", color = Color(0xFF6C3BFF))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF1E1E24)
        )
    }
}

@Composable
fun FilterChipItem(text: String, isSelected: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) Color(0xFF6C3BFF) else Color(0xFF2A2A35))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArtistItem(name: String, imageUrl: String, onLongClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.combinedClickable(
            onClick = { /* Do nothing for now */ },
            onLongClick = onLongClick
        )
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = name, color = Color.White, fontSize = 12.sp)
    }
}

@Composable
fun PlaylistItem(name: String, imageUrl: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable { onClick() }
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
