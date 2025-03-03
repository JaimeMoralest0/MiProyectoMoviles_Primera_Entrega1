package com.example.recetasapp.data

// Receta en la base de datos
data class Recipe(
    val id: String, // Identificador único de la receta
    val title: String, // Nombre de la receta
    val ingredients: List<String> // Lista de ingredientes que componen la receta
)

// Ingrediente dentro de una receta
data class Ingredient(
    val id: String, // Identificador único del ingrediente
    val name: String, // Nombre del ingrediente
    val quantity: String // Cantidad del ingrediente en la receta
)
