package com.example.version00.ui.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private const val BASE_URL = "https://deporte-main-production-75f.up.railway.app"

    val api: EjerciciosApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EjerciciosApi::class.java)
    }
}

object RetrofitClient2 {
    private const val BASE_URL = "https://gnews.io/api/"

    val api: GNewsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GNewsApi::class.java)
    }
}


class NutritionRepository(private val api: TheMealDBApi) {
    suspend fun getMeals(query: String = "") = api.searchMeals(query)
}

