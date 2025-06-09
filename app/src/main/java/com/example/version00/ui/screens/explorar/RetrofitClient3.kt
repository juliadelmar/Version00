package com.example.version00.ui.screens.explorar
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
object NutritionixRetrofitClient {
    private val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    private val client = OkHttpClient.Builder().addInterceptor(logging).build()

    val api: NutritionixApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://trackapi.nutritionix.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NutritionixApiService::class.java)
    }
}



interface NutritionixApiService {

    @GET("v2/search/instant")
    suspend fun searchSupplements(
        @Query("query") query: String = "supplement",
        @Header("x-app-id") appId: String,
        @Header("x-app-key") apiKey: String,
        @Header("x-remote-user-id") userId: String = "0"
    ): SupplementResponse

    companion object {
        const val BASE_URL = "https://trackapi.nutritionix.com/"
    }
}
