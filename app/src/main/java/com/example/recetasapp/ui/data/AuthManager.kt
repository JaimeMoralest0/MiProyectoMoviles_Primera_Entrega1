package com.example.recetasapp.ui.data

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.example.recetasapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthManager(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthRes<FirebaseUser> {
        return try {
            val result = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
            AuthRes.Success(result.user!!)
        } catch (e: Exception) {
            AuthRes.Error(e.message ?: "Error al registrar usuario")
        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthRes<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            AuthRes.Success(authResult.user!!)
        } catch (e: Exception) {
            AuthRes.Error(e.message ?: "Error al iniciar sesión")
        }
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun signOut() {
        auth.signOut()
        googleSignInClient.signOut()
        googleSignInClient.revokeAccess()
    }

    suspend fun resetPassword(email: String): AuthRes<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            AuthRes.Success(Unit)
        } catch (e: Exception) {
            AuthRes.Error(e.message ?: "Error al restablecer la contraseña")
        }
    }

    suspend fun signInAnonymously(): AuthRes<FirebaseUser?> {
        return try {
            val user = auth.signInAnonymously().await().user
            AuthRes.Success(user)
        } catch (e: Exception) {
            AuthRes.Error(e.message ?: "Error al iniciar sesión anónima")
        }
    }

    suspend fun handleSignInResult(task: Task<GoogleSignInAccount>): AuthRes<FirebaseUser?> {
        return try {
            println("Procesando resultado de Google Sign In")
            val account = task.getResult(ApiException::class.java)
                ?: return AuthRes.Error("Error: No se pudo obtener la cuenta de Google")

            println("Cuenta de Google obtenida:")
            println("Email: ${account.email}")
            println("Nombre: ${account.displayName}")
            println("Token ID presente: ${account.idToken != null}")

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            println("Credencial de Firebase creada")

            try {
                println("Iniciando autenticación con Firebase")
                val result = auth.signInWithCredential(credential).await()
                result.user?.let {
                    println("Autenticación exitosa con Firebase")
                    println("Usuario: ${it.email}")
                    AuthRes.Success(it)
                } ?: AuthRes.Error("Error: Usuario nulo después de la autenticación")
            } catch (e: Exception) {
                println("Error en autenticación con Firebase: ${e.message}")
                e.printStackTrace()
                AuthRes.Error("Error al autenticar con Firebase: ${e.message}")
            }
        } catch (e: ApiException) {
            println("ApiException en Google Sign In")
            println("Código de error: ${e.statusCode}")
            println("Mensaje: ${e.message}")
            e.printStackTrace()
            when (e.statusCode) {
                GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> {
                    println("El usuario canceló el inicio de sesión")
                    AuthRes.Error("El inicio de sesión fue cancelado")
                }
                GoogleSignInStatusCodes.NETWORK_ERROR -> AuthRes.Error("Error de red. Comprueba tu conexión")
                else -> AuthRes.Error("Error en Google Sign In (${e.statusCode}): ${e.message}")
            }
        } catch (e: Exception) {
            println("Error inesperado en Google Sign In")
            println("Tipo de error: ${e.javaClass.simpleName}")
            println("Mensaje: ${e.message}")
            e.printStackTrace()
            AuthRes.Error("Error inesperado: ${e.message}")
        }
    }

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    // Inicia el proceso de inicio de sesión con Google
    fun signInWithGoogle(googleSignInLauncher: ActivityResultLauncher<Intent>) {
        try {
            println("Iniciando proceso de Google Sign In")
            // Usar la instancia lazy en lugar de crear una nueva
            val signInIntent = googleSignInClient.signInIntent
            println("Intent de Google Sign In creado")
            googleSignInLauncher.launch(signInIntent)
        } catch (e: Exception) {
            println("Error al iniciar Google Sign In: ${e.message}")
            e.printStackTrace()
        }
    }
}

//  Definición de AuthRes sin modificar el código existente
sealed class AuthRes<out T> {
    data class Success<T>(val data: T) : AuthRes<T>()
    data class Error(val errorMessage: String) : AuthRes<Nothing>()
}
