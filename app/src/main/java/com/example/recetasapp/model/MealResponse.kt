package com.example.recetasapp.model

// Modelo que representa la respuesta de la API cuando se consultan comidas
data class MealResponse(
    val meals: List<Meal>? // Lista de comidas obtenidas de la API (puede ser nula si no hay resultados)
)
