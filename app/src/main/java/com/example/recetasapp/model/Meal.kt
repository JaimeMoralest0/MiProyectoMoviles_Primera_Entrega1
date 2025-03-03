package com.example.recetasapp.model

//Comida obtenida de la API
data class Meal(
    val idMeal: String, // Identificador único de la comida en la API
    val strMeal: String, // Nombre de la comida
    val strMealThumb: String, // URL de la imagen de la comida
    val strInstructions: String? = null // Instrucciones opcionales de preparación
)
