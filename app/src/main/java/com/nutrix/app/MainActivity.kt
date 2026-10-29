package com.nutrix.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nutrix.app.core.designsystem.NutrixTheme
import com.nutrix.app.navigation.NutrixNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as NutrixApp
        val repository = app.repository

        setContent {
            NutrixTheme {
                NutrixNavGraph(repository = repository)
            }
        }
    }
}
