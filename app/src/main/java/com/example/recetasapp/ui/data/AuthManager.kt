package com.example.recetasapp.ui.data

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.example.recetasapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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
    private val auth: FirebaseAuth by lazy { Firebase.auth }

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
            val account = task.getResult(ApiException::class.java)

            // Verificamos si la cuenta es válida
            if (account == null) {
                return AuthRes.Error("Error al iniciar sesión con Google: Cuenta nula")
            }

            // Generamos las credenciales de Google con el token ID
            val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)

            //  autenticar al usuario en Firebase
            val firebaseUser = auth.signInWithCredential(credential).await()
            firebaseUser.user?.let {
                AuthRes.Success(it)
            } ?: throw Exception("Usuario nulo")
        } catch (e: ApiException) {
            AuthRes.Error(e.message ?: "Error al iniciar sesión con Google: ApiException")
        } catch (e: Exception) {
            AuthRes.Error(e.message ?: "Error al iniciar sesión con Google")
        }
    }

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) // Verifica que esté configurado correctamente
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    // Inicia el proceso de inicio de sesión con Google
    fun signInWithGoogle(googleSignInLauncher: ActivityResultLauncher<Intent>) {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }
}

//  Definición de AuthRes sin modificar el código existente
sealed class AuthRes<out T> {
    data class Success<T>(val data: T) : AuthRes<T>()
    data class Error(val errorMessage: String) : AuthRes<Nothing>()
}
