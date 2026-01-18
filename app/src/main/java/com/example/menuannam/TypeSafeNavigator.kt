package com.example.menuannam

import kotlinx.serialization.Serializable

 /*
 =================================================================
 1. NAVIGATION ROUTES
 =================================================================
 These classes/objects are used by the Navigation library to define
 screens and pass arguments in a type-safe way.
 */

// --- Simple Routes (No Arguments) ---
@Serializable
object MainRoute    // Home Screen

@Serializable
object StudyRoute   // Study/Flashcard Session Screen

@Serializable
object AddRoute     // Add New Card Screen

@Serializable
object SearchRoute  // Search Input Screen

@Serializable
object LogInRoute   // Email Entry Screen

// --- Routes with Arguments (Passing Data) ---

@Serializable
data class FlashCardRoute(
    val english: String,
    val enWord: Boolean, // True = Exact Match, False = Contains
    val vietnamese: String,
    val vnWord: Boolean
)

@Serializable
data class ShowCardRoute(
    val english: String,
    val vietnamese: String
)

@Serializable
data class EditCardRoute(
    val englishOld: String,
    val vietnameseOld: String
)

@Serializable
data class TokenRoute(
    val email: String
)

 /*
 =================================================================
 2. DATA MODELS (API Request/Response)
 =================================================================
 These data classes are used for parsing JSON to/from the Network Service.
 */

@Serializable
data class UserCredential(
    val email: String
)

@Serializable
data class Token(
    val code: Int,
    val message: String // Usually contains the status message
)

@Serializable
data class Vocabulary(
    val email: String,
    val token: String,
    val word: String
)

@Serializable
data class Audio(
    val code: Int,
    val message: String // Contains the Base64 encoded audio string
)