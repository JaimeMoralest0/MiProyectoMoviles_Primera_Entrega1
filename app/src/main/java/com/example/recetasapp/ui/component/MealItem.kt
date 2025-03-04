package com.example.recetasapp.ui.component

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.recetasapp.model.Meal
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.graphics.Color
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun MealItem(
    meal: Meal,
    context: Context,
    onClick: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberImagePainter(meal.strMealThumb),
                    contentDescription = meal.strMeal,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = meal.strMeal,
                    style = MaterialTheme.typography.bodyLarge
                )

                // Botón de Compartir
                IconButton(
                    onClick = {
                        shareMeal(context, meal)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Compartir receta",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Botón Naranja para añadir a favoritos
            Button(
                onClick = {
                    db.collection("favoritos").document(meal.idMeal).set(meal)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Añadido correctamente a favoritos", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error al añadir a favoritos", Toast.LENGTH_SHORT).show()
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)) // Naranja
            ) {
                Text(text = "Añadir a favoritos", color = Color.White)
            }
        }
    }
}

fun shareMeal(context: Context, meal: Meal) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "¡Mira esta deliciosa receta que puedes realizar en RecetApp: ${meal.strMeal}! 🥘")
    }
    context.startActivity(Intent.createChooser(shareIntent, "Compartir receta"))
}
