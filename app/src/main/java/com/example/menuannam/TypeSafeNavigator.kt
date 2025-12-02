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
data class ShowCardRoute(val english: String, val vietnamese: String)

// Route có argument: id của FlashCard
//@Serializable
//data class ShowCardRoute(val cardId: Int)

