package com.example.recetasapp.network

import com.example.recetasapp.model.MealResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Interfaz que define las llamadas a la API de comidas
interface MealService {

    // Metodo para obtener los detalles de una comida específica por su ID
    @GET("lookup.php")
    suspend fun getMealById(@Query("i") id: String): MealResponse

    // Metodo para obtener comidas filtradas por ingrediente
    @GET("filter.php")
    suspend fun getMealsByIngredient(@Query("i") ingredient: String): MealResponse

    companion object {
        // Metodo para crear una instancia de Retrofit y conectar con la API
        fun create(): MealService {
            return Retrofit.Builder()
                .baseUrl("https://www.themealdb.com/api/json/v1/1/") // URL base de la API
                .addConverterFactory(GsonConverterFactory.create()) // Convertidor de JSON a objetos Kotlin
                .build()
                .create(MealService::class.java) // Se crea la instancia del servicio
        }
    }
}
