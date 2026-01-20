package com.example.menuannam

import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Url

interface NetworkService {

    /*
     =================================================================
     1. GENERATE TOKEN (LOGIN)
     =================================================================
     Sends the user's email to the Lambda function to request an OTP/Token.
     The @Url parameter overrides the base URL defined in Retrofit Client.
    */
    @PUT
    suspend fun generateToken(
        // If your Lambda is in Sydney, change "ap-southeast-1" to "ap-southeast-2"
        @Url url: String = "https://egsbwqh7kildllpkijk6nt4soq0wlgpe.lambda-url.ap-southeast-1.on.aws/",

        // The Request Body (JSON) containing the email
        @Body email: UserCredential
    ): Token

    /*
     =================================================================
     2. GENERATE AUDIO (TEXT-TO-SPEECH)
     =================================================================
     Sends vocabulary text to Lambda to generate an MP3 audio file (Base64 encoded).
    */
    @PUT
    suspend fun generateAudio(
        // TODO: Check your AWS Region here too!
        @Url url: String = "https://ityqwv3rx5vifjpyufgnpkv5te0ibrcx.lambda-url.ap-southeast-1.on.aws/",

        // The Request Body (JSON) containing the word to speak
        @Body vocabulary: Vocabulary
    ): Audio
}