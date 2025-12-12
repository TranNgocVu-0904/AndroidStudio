package com.example.menuannam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.menuannam.ui.theme.MenuAnNamTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MenuAnNamTheme{
                val navigation = rememberNavController()
                val db = Room.databaseBuilder(
                    applicationContext,
                    MenuDatabase::class.java, "menuDatabase"
                ).build()
                val flashCardDao = db.flashCardDao()

                val retrofit: Retrofit = Retrofit.Builder()
                    .baseUrl("https://placeholder.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val networkService = retrofit.create(NetworkService::class.java)

                AppNavigation(
                    navigation, flashCardDao,networkService
                )
            }
        }
    }
}








