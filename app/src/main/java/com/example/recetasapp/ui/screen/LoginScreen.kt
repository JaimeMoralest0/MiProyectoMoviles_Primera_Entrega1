package com.example.recetasapp.ui.screen

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.recetasapp.ui.data.AuthManager
import com.example.recetasapp.ui.data.AuthRes
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(
    auth: AuthManager,
    navigateToHome: () -> Unit,
    navigateToRegister: () -> Unit,
    navigateToDatabase: () -> Unit // Se ha añadido correctamente
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }  // Estado para controlar la animación de carga
    val scope = rememberCoroutineScope() // Creamos un scope para la corutina
    val context = LocalContext.current

    // Maneja el resultado del intento de inicio de sesión de Google
    val googleSignLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        println("Resultado de Google Sign In: ${result.resultCode}")
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                scope.launch {
                    println("Iniciando proceso de autenticación con Google")
                    val account = auth.handleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(result.data))
                    when (account) {
                        is AuthRes.Success -> {
                            println("Autenticación exitosa: ${account.data?.email}")
                            Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                            navigateToHome()
                        }
                        is AuthRes.Error -> {
                            println("Error en la autenticación: ${account.errorMessage}")
                            Toast.makeText(context, "Error: ${account.errorMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            Activity.RESULT_CANCELED -> {
                println("El usuario canceló el inicio de sesión")
                Toast.makeText(context, "Inicio de sesión cancelado por el usuario", Toast.LENGTH_SHORT).show()
            }
            else -> {
                println("Resultado inesperado: ${result.resultCode}")
                Toast.makeText(context, "Error inesperado en el inicio de sesión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Iniciar sesión", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de "Iniciar sesión"
        Button(
            onClick = {
                scope.launch {
                    isLoading = true  // Mostrar el loader
                    signIn(email, password, context, auth, navigateToHome) {
                        isLoading = false // Ocultar el loader después de completar
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }

        // Botón de "Registrarse"
        Button(
            onClick = navigateToRegister,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text("Registrarse", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp)) // ✅ Espaciado adecuado

        // Botón de "Continuar con Google"
        SocialMediaButton(
            onClick = {
                auth.signInWithGoogle(googleSignLauncher)
            },
            text = "Continuar con Google",
            color = Color(0xFF4B0082)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para la base de datos
        Button(
            onClick = { navigateToDatabase() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text("Base de Datos", color = Color.White)
        }

        // Si el estado es "isLoading", muestra el indicador de carga
        if (isLoading) {
            Spacer(modifier = Modifier.height(32.dp))
            CircularProgressIndicator() // Loader
        }
    }
}

// Función para manejar el inicio de sesión con Firebase
suspend fun signIn(
    email: String,
    password: String,
    context: Context,
    auth: AuthManager,
    navigateToHome: () -> Unit,
    onComplete: () -> Unit
) {
    if (email.isNotEmpty() && password.isNotEmpty()) {
        val result =
            withContext(kotlinx.coroutines.Dispatchers.IO) {
                auth.signInWithEmailAndPassword(email, password)
            }
        when (result) {
            is AuthRes.Success -> {
                Toast.makeText(context, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show()
                navigateToHome()
            }
            is AuthRes.Error -> {
                Toast.makeText(context, result.errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    } else {
        Toast.makeText(context, "Email y password tienen que estar rellenos", Toast.LENGTH_SHORT).show()
    }
    onComplete()  // Termina la animación de carga
}

@Composable
fun SocialMediaButton(onClick: () -> Unit, text: String, color: Color) {
    var click by remember { mutableStateOf(false) }
    Surface(
        onClick = onClick,
        modifier = Modifier
            .padding(start = 40.dp, end = 40.dp)
            .clickable { click = !click },
        shape = RoundedCornerShape(50),
        color = color
    ) {
        Row(
            modifier = Modifier
                .padding(start = 12.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text)
            click = true
        }
    }
}
