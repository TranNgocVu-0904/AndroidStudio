package com.example.menuannam

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.menuannam.ui.theme.MenuAnNamTheme
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val Context.dataStore by preferencesDataStore(
    name = "user_credentials"
)
val TOKEN = stringPreferencesKey("token")
val EMAIL = stringPreferencesKey("email")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MenuAnNamTheme{
                val navigation = rememberNavController()

                val appContext = applicationContext

                val db = Room.databaseBuilder(
                    appContext,
                    MenuDatabase::class.java, "menuDatabase"
                ).build()

                val flashCardDao = db.flashCardDao()

                // Network
                // Create a single OkHttpClient instance
                val sharedOkHttpClient = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS) // Set connection timeout
                    .readTimeout(30, TimeUnit.SECONDS)    // Set read timeout
                    .build()
                /*
                 Create the first Retrofit instance, using the shared OkHttpClient
                .client(sharedOkHttpClient) // Pass the shared client

                 Retrofit requires a valid HttpUrl: The baseUrl() method of Retrofit.Builder expects an okhttp3.HttpUrl object.
                 This object represents a well-formed URL and requires a scheme (like "http" or "https"),
                 a host, and optionally a port and path. It cannot be null or an empty string.
                 You can use a placeholder or dummy URL, such as http://localhost/ or http://example.com/,
                 during the initial setup. This satisfies Retrofit's requirement for a valid base URL.
                 */
                val retrofit: Retrofit = Retrofit.Builder()
                    .baseUrl("https://placeholder.com")
                    .client(sharedOkHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                // Create an implementation of the API endpoints defined by the service interface.
                val networkService = retrofit.create(NetworkService::class.java)

                AppNavigation(
                    navigation, flashCardDao,networkService
                )
            }
        }
    }
}








