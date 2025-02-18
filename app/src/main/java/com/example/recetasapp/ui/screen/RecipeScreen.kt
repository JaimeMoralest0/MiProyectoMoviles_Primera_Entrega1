package com.example.recetasapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.recetasapp.RecipeViewModel
import com.example.recetasapp.model.Meal
import com.example.recetasapp.ui.Navegacion.Login
import com.example.recetasapp.ui.component.MealItem
import com.example.recetasapp.ui.data.AuthManager

@Composable
fun RecipeScreen(navController: NavController, auth: AuthManager, viewModel: RecipeViewModel = viewModel()) {

    var ingredient by remember { mutableStateOf("") }  // Estado del ingrediente
    val meals by viewModel.meals.collectAsState()  // Obtener las comidas desde el ViewModel

    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = {
                auth.signOut() // Cierra sesión en Firebase
                navController.navigate(Login::class.simpleName!!) {
                    popUpTo(0) // Elimina el historial de navegación
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Cerrar Sesión", color = Color.White)
        }



        // Campo de texto para buscar ingredientes
        OutlinedTextField(
            value = ingredient,
            onValueChange = { ingredient = it },
            label = { Text("Buscar ingrediente") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para buscar recetas
        Button(
            onClick = {
                if (ingredient.isNotEmpty()) {
                    // Llamo a la función en el ViewModel para obtener las recetas
                    viewModel.getMealsByIngredient(ingredient)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar recetas")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // LazyColumn para mostrar las recetas
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(meals) { meal: Meal ->
                MealItem(meal)  // Llamo a MealItem para cada receta
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val fakeAuthManager = AuthManager(context = androidx.compose.ui.platform.LocalContext.current)
    RecipeScreen(navController = rememberNavController(), auth = fakeAuthManager)
}
