package com.example.recetasapp.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.recetasapp.model.Meal

@Composable
fun MealItem(meal: Meal) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Cargar la imagen de la receta usando Coil
            Image(
                painter = rememberImagePainter(meal.strMealThumb),
                contentDescription = meal.strMeal,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = meal.strMeal, style = MaterialTheme.typography.bodyLarge)  // Mostrar el nombre de la receta
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MealItem(meal = Meal("1", "Chicken Soup", "https://example.com/image.jpg"))
}
