package com.example.menuannam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(
    changeMessage: (String) -> Unit = {},
    getRandomLesson: suspend (Int) -> List<FlashCard>,
    networkService: NetworkService,
    email: String,
    token: String
) {
    /*
     ==========================================
     1. STATE MANAGEMENT
     ==========================================
    */
    // Holds the list of cards for the current study session
    var lessonCards by remember { mutableStateOf<List<FlashCard>>(emptyList()) }

    // Tracks which card is currently being displayed
    var currentIndex by remember { mutableStateOf(0) }

    // Toggles between English (Question) and Vietnamese (Answer)
    var showEnglish by remember { mutableStateOf(true) }

    // Tracks if the audio file exists for the current word
    var audioFile by remember { mutableStateOf(false) }

    // Enable audio features only if user is logged in
    val audioUse = email.isNotBlank() && token.isNotBlank()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    /*
     ==========================================
     2. INITIALIZATION (On Screen Load)
     ==========================================
    */
    LaunchedEffect(Unit) {
        try {
            if (email.isBlank() || token.isBlank()) {
                changeMessage("Missing email/token. Please login first.")
            }
            // B. Fetch Random Lesson (Limit 3 cards)
            val cards = getRandomLesson(3)
            lessonCards = cards

            if (cards.isEmpty()) {
                changeMessage("There are no flash cards in your database.")
            } else {
                // Reset state for the new lesson
                currentIndex = 0
                showEnglish = true
            }
        } catch (e: Exception) {
            changeMessage("Unexpected error while generating lesson.")
        }
    }
    /*
     ==========================================
     3. MAIN UI LAYOUT
     ==========================================
    */
    Column(
        modifier = Modifier.padding(12.dp).fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Only render if we have cards to show
        if (lessonCards.isNotEmpty()) {

            val currentCard = lessonCards[currentIndex]

            // Determine which side of the card to show
            val textToShow =
                if (showEnglish) {
                    currentCard.englishCard.orEmpty()
                }
                else {
                    currentCard.vietnameseCard.orEmpty()
                }

            // --- CARD FACE (Click to Flip) ---
            OutlinedButton(
                onClick = { showEnglish = !showEnglish },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = textToShow, style = MaterialTheme.typography.titleLarge)
            }

            // --- NEXT BUTTON ---
            // Only show "Next" when the answer (Vietnamese) is revealed
            if (!showEnglish) {
                Button(
                    onClick = {
                        if (currentIndex == lessonCards.lastIndex) {
                            // If end of list, shuffle and restart
                            lessonCards = lessonCards.shuffled()
                            currentIndex = 0
                        } else {
                            currentIndex++
                        }
                        // Always reset to English side for the new card
                        showEnglish = true
                    },
                    modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Next" }
                ) {
                    Text("Next", style = MaterialTheme.typography.titleLarge)
                }
            }
            /*
             ==========================================
             4. AUDIO LOGIC
             ==========================================
            */
            val vietnameseWord = currentCard.vietnameseCard.orEmpty()
            val filename = audioFilenameForWord(vietnameseWord)
            val file = File(context.filesDir, filename)

            // Check if file exists whenever the filename changes
            LaunchedEffect(filename) {
                audioFile = file.exists()
            }
            // CASE A: File does NOT exist -> Show Generate Button
            if (!audioFile) {
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val vocabulary = Vocabulary(word = vietnameseWord, email = email, token = token)

                                // Call API
                                val audio = withContext(Dispatchers.IO)
                                {
                                    networkService.generateAudio(
                                        vocabulary = vocabulary)
                                }
                                // Handle Response
                                if (audio.code == 200) {
                                    val bytes = decodeBase64(audio)
                                    if (bytes != null) {
                                        withContext(Dispatchers.IO) {
                                            saveAudioToInternalStorage(context, bytes, filename)
                                        }
                                        audioFile = true
                                        changeMessage("Generated")
                                    } else {
                                        changeMessage("Decode failed")
                                    }
                                } else {
                                    changeMessage(audio.message)
                                }
                            } catch (e: Exception) {
                                changeMessage("Generate failed: ${e.message}")
                            }
                        }

                    },
                    enabled = audioUse,
                    modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Generate" }
                )
                {
                    Text("Generate", style = MaterialTheme.typography.titleLarge)
                }
            }
            // CASE B: File EXISTS -> Show Play Button
            else {
                Button(
                    onClick = {
                        val file = File(context.filesDir, filename)
                        val filePath = file.absolutePath

                        // Create a Uri from the file path
                        val uri = filePath.toUri()

                        // Build the MediaItem
                        val mediaItem = MediaItem.fromUri(uri)

                        // Build the Player
                        val player = ExoPlayer.Builder(context).build()

                        player.addListener(object : Player.Listener {
                            override fun onPlaybackStateChanged(playbackState: Int) {
                                when (playbackState) {
                                    Player.STATE_BUFFERING -> {
                                        // Player is buffering, show a loading indicator if desired
                                        changeMessage("Buffering...")
                                    }

                                    Player.STATE_READY -> {
                                        // Player is prepared and ready to play
                                        changeMessage("Ready")
                                    }

                                    Player.STATE_ENDED -> {
                                        // Playback has finished
                                        player.release()
                                        changeMessage("Finished")
                                    }
                                    Player.STATE_IDLE -> {
                                        // Player is idle, e.g., after release or error
                                    }
                                }
                            }
                        })
                        // Set the media item to the player and prepare
                        player.setMediaItem(mediaItem)

                        // Prepare the player.
                        player.prepare()

                        // Start the playback.
                        player.play()
                    },
                    enabled = audioUse,
                    modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Play" }
                ) {
                    Text("Play", style = MaterialTheme.typography.titleLarge)
                }
            }
        }
    }
}