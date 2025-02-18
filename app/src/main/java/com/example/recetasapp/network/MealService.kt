package com.example.recetasapp.network

import com.example.recetasapp.model.MealResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Base URL de la API
private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

interface MealService {

    @GET("filter.php")
    suspend fun getMealsByIngredient(@Query("i") ingredient: String): MealResponse

    companion object {
        // Crear la instancia de Retrofit
        fun create(): MealService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(MealService::class.java)
        }
    }
}
