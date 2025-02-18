package com.example.recetasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.recetasapp.ui.Navegacion.SetupNavGraph
import com.example.recetasapp.ui.data.AuthManager
import com.example.recetasapp.ui.screen.LoginScreen
import com.example.recetasapp.ui.screen.RecipeScreen
import com.example.recetasapp.ui.theme.RecetasAppTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa Firebase
        FirebaseApp.initializeApp(this)

        val auth = AuthManager(this)

        setContent {
            RecetasAppTheme {
                Surface(modifier = androidx.compose.ui.Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // Setup Navigation
                    SetupNavGraph(auth)
                }
            }
        }
    }
}
