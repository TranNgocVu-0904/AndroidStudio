package com.example.menuannam

import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Url

interface NetworkService {
    @PUT
    suspend fun generateToken(
        @Url url: String = "https://dmzrueciplycef2h5r7ipqbf4y0hhpse.lambda-url.ap-southeast-1.on.aws/",
        @Body email: UserCredential
    ): Token
}