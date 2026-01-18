package com.example.menuannam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun EditCardScreen(
    // Parameters to identify the card to edit
    englishOld: String,
    vietnameseOld: String,

    // Callbacks for database operations
    getFlashCardByPair: suspend (String, String) -> FlashCard?,
    updateFlashCardByPair: suspend (String, String, String, String) -> Unit,
    changeMessage: (String) -> Unit,
    networkService: NetworkService,
    email : String,
    token : String
) {
    // State to hold the current FlashCard object fetched from DB
    var card by remember { mutableStateOf<FlashCard?>(null) }

    // UI States for TextFields
    var englishText by remember { mutableStateOf("") }
    var vietnameseText by remember { mutableStateOf("") }

    // State to track audio status: contains filename if exists, empty string if not
    var audioText by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    val isLoggedIn = email.isNotBlank() && token.isNotBlank()


    // Set initial instructional message
    LaunchedEffect(Unit) {
        changeMessage("Please edit the flashcard")
        if (!isLoggedIn) {
            changeMessage("Login for audio features.")
        }
    }

    // Load the specific FlashCard from Database using the passed arguments
    LaunchedEffect(englishOld, vietnameseOld) {

        val loaded = getFlashCardByPair(englishOld, vietnameseOld)

        card = loaded

        if (loaded != null) {
            // Populate text fields with current data
            englishText = loaded.englishCard ?: ""
            vietnameseText = loaded.vietnameseCard ?: ""
        }
        // Set initial audio filename based on Vietnamese text
        audioText = audioFilenameForWord(vietnameseText)
    }

    // Show loading text while DB query is running
    if (card == null) {
        Text("Loading...")
        return
    }

    // Main UI Layout
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Calculate expected filename based on current input
        val fileName = audioFilenameForWord(vietnameseText)
        val file = File(context.filesDir, fileName)
        /*
            File Existence Check
            This runs safely whenever 'fileName' changes (e.g., user types in Vietnamese field).
            It updates 'audioText' state based on whether the file physically exists on disk.
        */

        // This effect checks if the audio file exists whenever the vietnamese text changes
        LaunchedEffect(fileName) {
            audioText = if (
                file.exists()
                )
            {
                fileName
            } else{
                ""
            }
        }
        // Input Field: English
        TextField(
            value = englishText,
            onValueChange = { englishText = it },
            enabled = true,
            label = { Text(stringResource(R.string.English_Label)) },
            placeholder = { Text("Enter text") },
            modifier = Modifier.semantics { contentDescription = "English Input" }.fillMaxWidth()
        )

        // Input Field: Vietnamese
        TextField(
            value = vietnameseText,
            onValueChange = { vietnameseText = it },
            enabled = true,
            label = { Text(stringResource(R.string.Vietnamese_Label)) },
            placeholder = { Text("Nhập nội dung") },
            modifier = Modifier.semantics { contentDescription = "Vietnamese Input" }.fillMaxWidth()
        )

        // Read-only Field: Shows audio filename
        TextField(
            value = audioText,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Audio file") },
            modifier = Modifier.semantics { contentDescription = "Audio Input" }.fillMaxWidth()
        )

        // UPDATE BUTTON: Saves changes to Database
        Button(
            onClick = {
                scope.launch {
                    updateFlashCardByPair(
                        englishOld,
                        vietnameseOld,
                        englishText,
                        vietnameseText
                    )
                    changeMessage("Card updated")
                }
            },
            modifier = Modifier.width(160.dp).semantics { contentDescription = "Update" }
        ) {
            Text("Update flashcard")
        }

        // --- AUDIO CONTROLS SECTION ---

        // CASE A: Audio file exists (audioText is not empty)
        // Show "Clean" and "Play" buttons
        if (audioText.isNotEmpty()) {

            // CLEAN BUTTON: Deletes the file from internal storage
            Button(
                onClick = {
                    scope.launch {
                        val file = File(context.filesDir, audioText)

                        if (file.exists())
                            if (file.delete()){
                                // Crucial: Update state to empty to refresh UI (hide Play, show Generate)
                                audioText = ""
                                changeMessage("Audio file deleted")}
                            else
                                changeMessage("Delete failed")
                        else
                            changeMessage("Audio file not found")
                    }
                },
                modifier = Modifier.width(160.dp).semantics { contentDescription = "Clean" }
            ) {
                Text("Clean Audio")
            }

            // PLAY BUTTON: Plays audio using ExoPlayer
            Button(
                onClick = {
                    val file = File(context.filesDir, audioText)
                    val filePath = file.absolutePath

                    // Note: Ideally use Uri.fromFile(file) here for better ExoPlayer compatibility
                    val uri = filePath.toUri()

                    // Setup Player
                    val mediaItem = MediaItem.fromUri(uri)
                    val player = ExoPlayer.Builder(context).build()

                    player.addListener(object : Player.Listener {

                        override fun onPlaybackStateChanged(playbackState: Int) {

                            when (playbackState) {

                                Player.STATE_BUFFERING -> {
                                    changeMessage("Buffering...")
                                }
                                Player.STATE_READY -> {
                                    changeMessage("Ready")
                                }
                                Player.STATE_ENDED -> {
                                    player.release() // Release resources when done
                                    changeMessage("Finished")
                                }
                                Player.STATE_IDLE -> {}
                            }
                        }
                    })
                    // Start Playback
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()
                },
                enabled = true,
                modifier = Modifier.width(160.dp).semantics { contentDescription = "Play" }
            ) {
                Text("Play Audio")
            }
        }

        // CASE B: Audio file does NOT exist
        // Show "Generate" button
        if (audioText.isEmpty()) {
            Button(
                onClick = {
                    scope.launch {
                        try {
                            val vocabulary = Vocabulary(word = vietnameseText, email = email, token = token)

                            // Call API to generate audio
                            val audio = withContext(Dispatchers.IO)
                            {
                                networkService.generateAudio(vocabulary = vocabulary)
                            }

                            // Handle API Response
                            if (audio.code == 200) {

                                val bytes = decodeBase64(audio)

                                if (bytes != null) {
                                    // Save file to Internal Storage
                                    withContext(Dispatchers.IO) {
                                        saveAudioToInternalStorage(context, bytes, fileName)
                                    }
                                    // Update state to trigger UI refresh (Switch to Play button)
                                    audioText = fileName

                                    changeMessage("Generated")
                                } else {
                                    changeMessage("Decode failed")
                                }
                            } else {
                                changeMessage(audio.message)
                            }
                        } catch (e: Exception) {
                            changeMessage("Generate failed: Missing email/token. Please login first.")
                        }
                    }

                },
                enabled = true,
                modifier = Modifier.width(160.dp).semantics { contentDescription = "Generate" }
            )
            {
                Text("Generate audio")
            }
        }
    }
}