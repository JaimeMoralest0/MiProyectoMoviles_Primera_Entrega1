package com.example.recetasapp.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recetasapp.ui.data.AuthManager
import com.example.recetasapp.ui.data.AuthRes
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(auth: AuthManager, navController: NavController) {
    var email by remember { mutableStateOf("") } // Estado del email
    var password by remember { mutableStateOf("") } // Estado de la contraseña
    val scope = rememberCoroutineScope() // Manejo de corrutinas
    val context = LocalContext.current // Contexto de la app

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Registro", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        // Campo de entrada de email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de entrada de contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation() // Oculta la contraseña
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de registro en Firebase
        Button(
            onClick = {
                scope.launch {
                    registerUser(email, password, context, auth, navController)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text("Registrarse", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para volver a la pantalla de inicio de sesión
        TextButton(onClick = { navController.navigate("login") }) {
            Text("Volver a inicio de sesión")
        }
    }
}

// Función para registrar usuario en Firebase con mensajes de error detallados
suspend fun registerUser(
    email: String,
    password: String,
    context: Context,
    auth: AuthManager,
    navController: NavController
) {
    if (email.isNotEmpty() && password.isNotEmpty()) {
        val result = auth.createUserWithEmailAndPassword(email, password)

        when (result) {
            is AuthRes.Success -> {
                Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true } // Evita volver atrás
                }
            }
            is AuthRes.Error -> {
                Toast.makeText(context, "Error: ${result.errorMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    } else {
        Toast.makeText(context, "Email y contraseña no pueden estar vacíos", Toast.LENGTH_SHORT).show()
    }
}
