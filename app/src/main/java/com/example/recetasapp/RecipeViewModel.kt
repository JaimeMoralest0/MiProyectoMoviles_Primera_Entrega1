package com.example.recetasapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recetasapp.model.Meal
import com.example.recetasapp.network.MealService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {

    private val _meals = MutableStateFlow<List<Meal>>(emptyList())  // Estado para las recetas
    val meals: StateFlow<List<Meal>> = _meals  // Exponemos el flujo como StateFlow

    private val mealService = MealService.create()  // Crear la instancia de MealService

    // Llamada a la API para obtener las recetas con el ingrediente
    fun getMealsByIngredient(ingredient: String) {
        viewModelScope.launch {
            try {
                val fetchedMeals = mealService.getMealsByIngredient(ingredient)
                _meals.value = fetchedMeals.meals ?: emptyList()  // Actualizar el flujo con las recetas obtenidas
            } catch (e: Exception) {
                _meals.value = emptyList()  // Si hay error, devolver una lista vacía
            }
        }
    }
}
