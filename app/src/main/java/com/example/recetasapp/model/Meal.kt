package com.example.recetasapp.model

//Comida obtenida de la API
data class Meal(
    val idMeal: String = "",
    val strMeal: String = "",
    val strMealThumb: String = "",
    val strInstructions: String? = null // Permitir valores nulos
)

