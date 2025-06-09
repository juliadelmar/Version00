package com.example.version00.ui.network
import com.example.version00.ui.model.Ejercicio
import com.example.version00.ui.model.GNewsResponse
import com.example.version00.ui.model.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface EjerciciosApi {
    @GET("api/ejercicios/raw")
    suspend fun obtenerEjercicios(): List<Ejercicio>
}
interface GNewsApi {
    @GET("v4/search")
    suspend fun searchNews(
        @Query("q") query: String = "fitness health",
        @Query("token") apiKey: String,
        @Query("lang") lang: String = "en",
        @Query("max") max: Int = 20
    ): GNewsResponse
}
interface TheMealDBApi {
    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String = ""): MealResponse
}