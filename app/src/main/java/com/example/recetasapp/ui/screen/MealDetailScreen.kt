package com.example.recetasapp.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.recetasapp.model.Meal

@Composable
fun MealDetailScreen(meal: Meal) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = meal.strMeal, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar imagen de la receta
        Image(
            painter = rememberImagePainter(meal.strMealThumb),
            contentDescription = meal.strMeal,
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Aquí pueden agregarse más detalles sobre la receta, como ingredientes e instrucciones
        Text(text = "Detalles de la receta", style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true)
@Composable
fun MealDetailScreenPreview() {
    // Vista previa para la pantalla de detalles de la receta
    MealDetailScreen(meal = Meal("1", "Chicken Soup", "https://example.com/image.jpg"))
}
