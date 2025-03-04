package com.example.recetasapp.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.recetasapp.RecipeViewModel
import com.example.recetasapp.model.Meal
import com.example.recetasapp.ui.component.MealItem
import com.example.recetasapp.ui.data.AuthManager
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RecipeScreen(navController: NavController, auth: AuthManager, viewModel: RecipeViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        if (!auth.isUserLoggedIn()) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val context = LocalContext.current
    var ingredient by remember { mutableStateOf("") }
    val meals by viewModel.meals.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD0F0C0))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = com.example.recetasapp.R.drawable.meal),
            contentDescription = "Icono de comida",
            modifier = Modifier
                .size(100.dp)
                .clickable { navController.navigate("login") }
                .padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                },
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Cerrar Sesión", color = Color.White)
            }

            if (auth.isUserLoggedIn()) {
                Button(
                    onClick = {
                        try {
                            navController.navigate("database")
                        } catch (e: Exception) {
                            println("Error de navegación: ${e.message}")
                        }
                    },
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B))
                ) {
                    Text("Base de Datos", color = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = {
                Log.d("RecipeScreen", "Navegando a FavoritosScreen")
                navController.navigate("favoritos")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Favoritos")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = ingredient,
            onValueChange = { ingredient = it },
            label = { Text("Buscar ingrediente") },
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (ingredient.isNotEmpty()) {
                    viewModel.getMealsByIngredient(ingredient)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar recetas")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(meals) { meal ->
                MealItem(
                    meal = meal,
                    context = context,
                    onClick = {
                        println("Navegando a detalle de: ${meal.idMeal}")
                        navController.navigate("detail/${meal.idMeal}")
                    }
                )
            }
        }
    }
}