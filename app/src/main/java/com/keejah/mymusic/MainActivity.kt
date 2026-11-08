package com.keejah.mymusic

import android.Manifest
import android.content.ComponentName
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = Color.Black) {
                    MusicApp()
                }
            }
        }
    }
}

@Composable
fun MusicApp() {
    val context = LocalContext.current
    var mediaController by remember { mutableStateOf<MediaController?>(null) }
    var showNowPlaying by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }

    // --- ARTIST STATE ---
    // We hold this here so it stays alive when you switch tabs
    val artists = remember {
        mutableStateListOf(
            Pair("Elizabeth", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200"),
            Pair("Alfred", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200"),
            Pair("Thomas", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200")
        )
    }

    // Connect to MusicService
    LaunchedEffect(Unit) {
        val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
        val future = MediaController.Builder(context, sessionToken).buildAsync()
        future.addListener({
            mediaController = future.get()
        }, ContextCompat.getMainExecutor(context))
    }

    val playSong = { uri: Uri? ->
        val finalUri = uri ?: Uri.parse("file:///sdcard/Music/test_song.mp3")
        mediaController?.let { controller ->
            controller.setMediaItem(MediaItem.fromUri(finalUri))
            controller.prepare()
            controller.play()
            isPlaying = true
            showNowPlaying = true
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) playSong(null)
    }

    val checkPermissionAndPlay = { uri: Uri? ->
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            playSong(uri)
        } else {
            permissionLauncher.launch(permission)
        }
    }

    if (showNowPlaying && mediaController != null) {
        NowPlayingScreen(
            player = mediaController!!,
            isPlaying = isPlaying,
            onPlayPauseClick = {
                if (isPlaying) mediaController?.pause() else mediaController?.play()
                isPlaying = !isPlaying
            },
            onBackClick = {
                showNowPlaying = false
            }
        )
    } else {
        MainScreen(
            artists = artists,
            onAddArtist = { name, url -> artists.add(Pair(name, url)) },
            onRemoveArtist = { name -> artists.removeAll { it.first == name } },
            onSongSelected = { uri -> checkPermissionAndPlay(uri) }
        )
    }
}
