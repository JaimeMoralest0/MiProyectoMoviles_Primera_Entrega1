package com.example.recetasapp.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.recetasapp.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavController,
    mealId: String,
    viewModel: RecipeViewModel
) {
    val meal by viewModel.selectedMeal.collectAsState() // Observa la comida seleccionada en el ViewModel

    // Llama a la función del ViewModel para obtener los datos de la comida por su ID
    LaunchedEffect(mealId) {
        viewModel.getMealById(mealId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = meal?.strMeal ?: "Cargando...") }, // Muestra el nombre de la comida
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Volver") // Botón para regresar a la pantalla anterior
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Permite hacer scroll si el contenido es grande
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            meal?.let { currentMeal ->
                // Imagen de la comida
                Image(
                    painter = rememberImagePainter(currentMeal.strMealThumb),
                    contentDescription = currentMeal.strMeal,
                    modifier = Modifier
                        .size(300.dp)
                        .padding(bottom = 16.dp)
                )

                // Nombre de la comida
                Text(
                    text = currentMeal.strMeal,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Sección de instrucciones
                Text(
                    text = "Instrucciones:",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Descripción de la receta
                Text(
                    text = currentMeal.strInstructions ?: "",
                    style = MaterialTheme.typography.bodyLarge
                )
            } ?: run {
                // Si los datos aún no se han cargado, muestra un indicador de carga
                CircularProgressIndicator()
            }
        }
    }
}
