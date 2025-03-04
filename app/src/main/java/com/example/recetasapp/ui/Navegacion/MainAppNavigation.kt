package com.example.recetasapp.ui.Navegacion

import DatabaseScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.recetasapp.ui.screen.*
import com.example.recetasapp.ui.data.AuthManager

@Composable
fun MainAppNavigation(navController: NavHostController, auth: AuthManager) {
    // Define el NavHost, que maneja la navegación dentro de la aplicación
    NavHost(
        navController = navController,
        startDestination = "login" // La pantalla de inicio es la de login
    ) {
        // Pantalla de inicio de sesión
        composable("login") {
            LoginScreen(
                auth = auth,
                navigateToHome = {
                    navController.navigate("recipe") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                navigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        // Pantalla de registro
        composable("register") {
            RegisterScreen(auth = auth, navController = navController)
        }

        // Pantalla de recetas
        composable("recipe") {
            RecipeScreen(navController = navController, auth = auth)
        }

        // Pantalla de base de datos
        composable("database") {
            if (auth.isUserLoggedIn()) {
                DatabaseScreen(navController = navController, viewModel = viewModel())
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }

        // Pantalla de favoritos
        composable("favoritos") {
            FavoritosScreen(navController = navController)
        }

        // Pantalla de detalles de una receta
        composable(
            route = "detail/{mealId}",
            arguments = listOf(navArgument("mealId") { type = NavType.StringType })
        ) { backStackEntry ->
            val mealId = backStackEntry.arguments?.getString("mealId") ?: return@composable
            DetailScreen(navController = navController, mealId = mealId, viewModel = viewModel())
        }
    }
}