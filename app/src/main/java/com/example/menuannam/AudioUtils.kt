package com.example.menuannam

import android.content.Context
import android.util.Base64
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

fun audioFilenameForWord(word: String): String {
    val bytes = MessageDigest.getInstance("SHA-256")
        .digest(word.toByteArray(Charsets.UTF_8))
    val hex = bytes.joinToString("") { "%02x".format(it) }
    return "$hex.mp3"
}

fun saveAudioToInternalStorage(context: Context, audioData: ByteArray, filename: String): File {
    val file = File(context.filesDir, filename)
    FileOutputStream(file).use { fos ->
        fos.write(audioData)
    }
    return file
}

fun decodeBase64(audio: Audio): ByteArray? {
    if (audio.code != 200)
        return null
    return try {
        val original = audio.message.substringAfter(",")
        Base64.decode(original, Base64.DEFAULT)
    } catch (e: IllegalArgumentException) {
        null
    }
}