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
                    // Navega a la pantalla de recetas después de iniciar sesión
                    navController.navigate("recipe") {
                        popUpTo("login") { inclusive = true } // Elimina la pantalla de login del historial
                    }
                },
                navigateToRegister = { /* Implementar si es necesario */ }
            )
        }

        // Pantalla de recetas
        composable("recipe") {
            RecipeScreen(navController = navController, auth = auth)
        }

        // Pantalla de base de datos de recetas
        composable("database") {
            if (auth.isUserLoggedIn()) {
                DatabaseScreen(
                    navController = navController,
                    viewModel = viewModel() // Se obtiene el ViewModel asociado
                )
            } else {
                // Si el usuario no está autenticado, lo redirige al login
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }

        // Pantalla de detalles de una receta
        composable(
            route = "detail/{mealId}",
            arguments = listOf(navArgument("mealId") { type = NavType.StringType })
        ) { backStackEntry ->
            val mealId = backStackEntry.arguments?.getString("mealId") ?: return@composable
            DetailScreen(
                navController = navController,
                mealId = mealId,
                viewModel = viewModel() // Se obtiene el ViewModel para la pantalla de detalles
            )
        }
    }
}
