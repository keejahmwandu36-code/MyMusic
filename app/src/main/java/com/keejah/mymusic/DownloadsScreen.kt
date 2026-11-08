package com.keejah.mymusic

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

@Composable
fun DownloadsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var urlText by remember { mutableStateOf("") }
    var statusText by remember { mutableStateOf("Paste a direct MP3 link above") }
    var isDownloading by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F11))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Download Music",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Enter a direct .mp3 URL to save it to your device.",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = urlText,
            onValueChange = { urlText = it },
            label = { Text("Paste MP3 URL here") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF6C3BFF),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color(0xFF6C3BFF),
                unfocusedLabelColor = Color.Gray
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isDownloading) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(0xFF6C3BFF),
                trackColor = Color(0xFF2A2A35)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                if (urlText.isNotBlank() && urlText.startsWith("http")) {
                    isDownloading = true
                    statusText = "Downloading..."
                    progress = 0f
                    
                    scope.launch(Dispatchers.IO) {
                        val success = downloadMp3(context, urlText) { p ->
                            progress = p
                        }
                        
                        launch(Dispatchers.Main) {
                            isDownloading = false
                            if (success) {
                                statusText = "Download complete! Check your Library."
                                urlText = ""
                            } else {
                                statusText = "Download failed. Check logs."
                            }
                        }
                    }
                } else {
                    statusText = "Please enter a valid URL starting with http"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isDownloading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C3BFF),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isDownloading) {
                Text("Downloading...", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Default.Download, contentDescription = "Download")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Download", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = statusText,
            color = if (isDownloading) Color(0xFF8DB4FF) else Color.Gray,
            fontSize = 14.sp
        )
    }
}

fun downloadMp3(context: Context, url: String, onProgress: (Float) -> Unit): Boolean {
    return try {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "Mozilla/5.0")
            .build()
        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            println("Download failed: HTTP ${response.code}")
            return false
        }

        val body = response.body ?: return false
        val contentType = response.header("Content-Type") ?: ""
        
        val isAudio = contentType.contains("audio") ||
                      contentType.contains("octet-stream") ||
                      contentType.contains("binary")

        if (!isAudio) {
            println("Download failed: Not an audio file. Content-Type: $contentType")
            return false
        }

        val totalBytes = body.contentLength()
        println("Download started. Total bytes: $totalBytes")

        val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        if (!musicDir.exists()) musicDir.mkdirs()
        
        // Clean up the name from the URL
        val cleanName = url.substringAfterLast("/").substringBefore("?").ifBlank { "song" }
        val safeName = cleanName.replace(Regex("[^A-Za-z0-9.]"), "_")
        val fileName = if (safeName.endsWith(".mp3")) safeName else "$safeName.mp3"
        
        val file = File(musicDir, fileName)
        
        val inputStream = body.byteStream()
        val outputStream = FileOutputStream(file)

        val buffer = ByteArray(8192)
        var bytesRead: Int
        var totalRead = 0L

        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
            totalRead += bytesRead
            if (totalBytes > 0) {
                onProgress(totalRead.toFloat() / totalBytes.toFloat())
            }
        }

        outputStream.flush()
        outputStream.close()
        inputStream.close()

        println("Download finished. Total read: $totalRead bytes. File size: ${file.length()}")

        if (file.length() < 10240) {
            println("File too small, deleting.")
            file.delete()
            return false
        }

        MediaScannerConnection.scanFile(
            context,
            arrayOf(file.absolutePath),
            arrayOf("audio/mpeg"),
            null
        )
        
        true
    } catch (e: Exception) {
        println("Download error: ${e.message}")
        e.printStackTrace()
        false
    }
}
