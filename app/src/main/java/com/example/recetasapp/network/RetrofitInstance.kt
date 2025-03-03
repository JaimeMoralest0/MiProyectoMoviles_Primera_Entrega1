import com.example.recetasapp.network.MealService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {

    // URL base de la API
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"


    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Se define la URL base
            .addConverterFactory(GsonConverterFactory.create()) // Convertidor de JSON a objetos Kotlin
            .build()
    }

    // Para realizar las llamadas a la API
    val mealService: MealService by lazy {
        retrofit.create(MealService::class.java)
    }
}
