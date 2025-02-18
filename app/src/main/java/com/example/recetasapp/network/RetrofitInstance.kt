import com.example.recetasapp.network.MealService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "www.themealdb.com/api/json/v1/1/filter.php?i=chicken_breast"  // URL base de la API

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Usamos Gson para convertir la respuesta de JSON
            .build()
    }

    // Instancia del servicio MealService
    val mealService: MealService by lazy {
        retrofit.create(MealService::class.java)
    }
}
