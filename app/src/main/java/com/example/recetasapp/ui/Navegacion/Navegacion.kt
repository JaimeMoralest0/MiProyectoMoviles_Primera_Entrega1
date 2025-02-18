package com.example.recetasapp.ui.Navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recetasapp.ui.data.AuthManager
import com.example.recetasapp.ui.screen.LoginScreen
import com.example.recetasapp.ui.screen.RecipeScreen
import com.example.recetasapp.ui.screen.RegisterScreen
import com.example.recetasapp.ui.screen.DatabaseScreen

@Composable
fun SetupNavGraph(auth: AuthManager) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Login::class.simpleName!!) {
        composable(Login::class.simpleName!!) {
            LoginScreen(
                auth,
                navigateToHome = { navController.navigate(ScreenRecipe::class.simpleName!!) },
                navigateToRegister = { navController.navigate(Register::class.simpleName!!) },
                navigateToDatabase = { navController.navigate("databaseScreen") } // ✅ Ahora usa un string válido
                // ✅ Se añade esta línea
            )
        }
        composable(ScreenRecipe::class.simpleName!!) {
            RecipeScreen(navController, auth)
        }
        composable(Register::class.simpleName!!) {
            RegisterScreen(auth, navController)
        }
        composable("databaseScreen") {
            DatabaseScreen(navController) // ✅ Se usa directamente, sin `simpleName!!`
        }

    }
}
