package com.example.menuannam

import kotlinx.serialization.Serializable

@Serializable
object MainRoute

@Serializable
object StudyRoute

@Serializable
object AddRoute

@Serializable
object SearchRoute

@Serializable
object LogInRoute

@Serializable
data class ShowCardRoute(val english: String, val vietnamese: String)

@Serializable
data class EditCardRoute(val englishOld: String, val vietnameseOld: String)

@Serializable
data class UserCredential(val email: String)

@Serializable
data class Token(val token: String)

/*
Route có argument: id của FlashCard
@Serializable
data class ShowCardRoute(val cardId: Int)
*/

