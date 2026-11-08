package com.keejah.mymusic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun NowPlayingScreen(
    player: Player,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val albumArtUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?q=80&w=1000&auto=format&fit=crop"

    // 1. State for time and duration
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }

    // 2. Update the time every 100ms
    LaunchedEffect(Unit) {
        while (true) {
            currentPosition = player.currentPosition
            duration = player.duration.coerceAtLeast(0L)
            delay(100)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F11))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Close", tint = Color.White)
                }
                Text(
                    text = "now playing",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Icon(Icons.Outlined.StarBorder, contentDescription = "Favorite", tint = Color.White)
            }

            AsyncImage(
                model = albumArtUrl,
                contentDescription = "Album Art",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(350.dp)
                    .clip(RoundedCornerShape(24.dp))
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .offset(y = (-50).dp)
                    .shadow(16.dp, RoundedCornerShape(16.dp))
                    .background(Color(0xCC2A2A35), RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "Test Song",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "SoundHelix",
                        color = Color(0xFFB0B0B8),
                        fontSize = 16.sp
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .offset(y = (-20).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Display current time
                Text(formatTime(currentPosition), color = Color.Gray, fontSize = 12.sp)
                
                Spacer(modifier = Modifier.weight(1f))
                
                // The waveform
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .width(200.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(30) { index ->
                        val height = remember { Random.nextInt(10, 40).dp }
                        // Change the color based on how far the song has played
                        val progress = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f
                        val isPlayed = (index.toFloat() / 30f) < progress
                        
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(height)
                                .background(
                                    if (isPlayed) Color(0xFF8DB4FF) else Color.DarkGray,
                                    RoundedCornerShape(50)
                                )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Display remaining time
                Text("-${formatTime((duration - currentPosition).coerceAtLeast(0L))}", color = Color.Gray, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp, vertical = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Shuffle */ }) {
                    Icon(Icons.Default.Shuffle, contentDescription = "Shuffle", tint = Color.White)
                }

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFF5C9DFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = Color.Black)
                }

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(20.dp, CircleShape, spotColor = Color(0xFF8DB4FF))
                        .background(Color.Black, CircleShape)
                        .border(2.dp, Color(0xFF8DB4FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onPlayPauseClick) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFF5C9DFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color.Black)
                }

                IconButton(onClick = { /* Repeat */ }) {
                    Icon(Icons.Default.Repeat, contentDescription = "Repeat", tint = Color.White)
                }
            }
        }
    }
}

// Helper function to convert milliseconds to MM:SS format
fun formatTime(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    return String.format("%d:%02d", minutes, seconds)
}
