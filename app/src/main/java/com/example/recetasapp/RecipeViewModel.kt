package com.example.recetasapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recetasapp.model.Meal
import com.example.recetasapp.network.MealService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {
    // Estado para almacenar la lista de comidas obtenidas
    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals: StateFlow<List<Meal>> = _meals

    // Estado para almacenar la comida seleccionada
    private val _selectedMeal = MutableStateFlow<Meal?>(null)
    val selectedMeal: StateFlow<Meal?> = _selectedMeal

    // Servicio para hacer llamadas a la API
    private val mealService = MealService.create()

    // Obtiene la lista de comidas por un ingrediente específico
    fun getMealsByIngredient(ingredient: String) {
        viewModelScope.launch {
            try {
                val fetchedMeals = mealService.getMealsByIngredient(ingredient)
                _meals.value = fetchedMeals.meals ?: emptyList() // Si la respuesta es nula, devuelve una lista vacía
            } catch (e: Exception) {
                _meals.value = emptyList() // Si hay un error, deja la lista vacía
            }
        }
    }

    // Obtiene una comida específica por su ID
    fun getMealById(id: String) {
        viewModelScope.launch {
            try {
                val response = mealService.getMealById(id)
                _selectedMeal.value = response.meals?.firstOrNull() // Obtiene la primera comida si existe
            } catch (e: Exception) {
                _selectedMeal.value = null // Si hay un error, deja el valor en null
            }
        }
    }
}
