package com.example.mangasduocuc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import com.example.mangasduocuc.navigation.AppNav
import com.example.mangasduocuc.ui.theme.MangasDuocTheme

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.mangasduocuc.di.RepositoryProvider.init(applicationContext)
        setContent {
            MangasDuocTheme {
                val windowSizeClass: WindowSizeClass = calculateWindowSizeClass(this)
                AppRoot(windowSizeClass)
            }
        }
    }
}

@Composable
fun AppRoot(windowSizeClass: WindowSizeClass) {
    Surface(color = MaterialTheme.colorScheme.background) {
        AppNav(windowSizeClass = windowSizeClass)
    }
}