package com.example.recetasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.recetasapp.ui.Navegacion.MainAppNavigation
import com.example.recetasapp.ui.data.AuthManager
import com.example.recetasapp.ui.theme.RecetasAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Establece el contenido de la actividad
        setContent {
            RecetasAppTheme {
                // Superficie principal de la aplicación con el fondo definido en el tema
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController() // Controlador de navegación
                    val auth = AuthManager(this) // Instancia de gestión de autenticación

                    // Configura la navegación de la aplicación
                    MainAppNavigation(navController = navController, auth = auth)
                }
            }
        }
    }
}
