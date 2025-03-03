package com.example.recetasapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.recetasapp.data.Recipe
import com.example.recetasapp.data.Ingredient

// ViewModel que gestiona la interacción con Firebase Firestore para la gestión de recetas e ingredientes
class DatabaseViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance() // Instancia de Firestore

    // Estado para almacenar la lista de recetas obtenidas de Firestore
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    // Estado para almacenar la lista de ingredientes obtenidos de Firestore
    private val _ingredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val ingredients: StateFlow<List<Ingredient>> = _ingredients

    // Inicializa la carga de datos desde Firestore cuando el ViewModel es creado
    init {
        loadRecipes()
        loadIngredients()
    }

    // Carga todas las recetas desde Firestore y actualiza el estado
    fun loadRecipes() {
        viewModelScope.launch {
            db.collection("recipes").get()
                .addOnSuccessListener { documents ->
                    val recipeList = documents.map { doc ->
                        Recipe(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            ingredients = doc.get("ingredients") as? List<String> ?: emptyList()
                        )
                    }
                    _recipes.value = recipeList
                }
        }
    }

    // Carga todos los ingredientes desde Firestore y actualiza el estado
    fun loadIngredients() {
        viewModelScope.launch {
            db.collection("ingredients").get()
                .addOnSuccessListener { documents ->
                    val ingredientList = documents.map { doc ->
                        Ingredient(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            quantity = doc.getString("quantity") ?: ""
                        )
                    }
                    _ingredients.value = ingredientList
                }
        }
    }

    // Agrega una nueva receta a Firestore y actualiza la lista de recetas
    fun addRecipe(title: String, ingredients: List<String>) {
        viewModelScope.launch {
            val recipe = hashMapOf(
                "title" to title,
                "ingredients" to ingredients
            )
            db.collection("recipes").add(recipe)
                .addOnSuccessListener { loadRecipes() } // Recarga las recetas después de agregar
        }
    }

    // Agrega un nuevo ingrediente a Firestore y actualiza la lista de ingredientes
    fun addIngredient(name: String, quantity: String) {
        viewModelScope.launch {
            val ingredient = hashMapOf(
                "name" to name,
                "quantity" to quantity
            )
            db.collection("ingredients").add(ingredient)
                .addOnSuccessListener { loadIngredients() } // Recarga los ingredientes después de agregar
        }
    }

    // Actualiza una receta existente en Firestore y actualiza la lista de recetas
    fun updateRecipe(id: String, title: String, ingredients: List<String>) {
        viewModelScope.launch {
            db.collection("recipes").document(id)
                .update(
                    mapOf(
                        "title" to title,
                        "ingredients" to ingredients
                    )
                )
                .addOnSuccessListener { loadRecipes() } // Recarga las recetas después de actualizar
        }
    }

    // Actualiza un ingrediente existente en Firestore y actualiza la lista de ingredientes
    fun updateIngredient(id: String, name: String, quantity: String) {
        viewModelScope.launch {
            db.collection("ingredients").document(id)
                .update(
                    mapOf(
                        "name" to name,
                        "quantity" to quantity
                    )
                )
                .addOnSuccessListener { loadIngredients() } // Recarga los ingredientes después de actualizar
        }
    }

    // Elimina una receta de Firestore y actualiza la lista de recetas
    fun deleteRecipe(id: String) {
        viewModelScope.launch {
            db.collection("recipes").document(id).delete()
                .addOnSuccessListener { loadRecipes() } // Recarga las recetas después de eliminar
        }
    }

    // Elimina un ingrediente de Firestore y actualiza la lista de ingredientes
    fun deleteIngredient(id: String) {
        viewModelScope.launch {
            db.collection("ingredients").document(id).delete()
                .addOnSuccessListener { loadIngredients() } // Recarga los ingredientes después de eliminar
        }
    }
}

// Modelo de datos para representar una receta
data class Recipe(
    val id: String,
    val title: String,
    val ingredients: List<String>
)

// Modelo de datos para representar un ingrediente
data class Ingredient(
    val id: String,
    val name: String,
    val quantity: String
)
