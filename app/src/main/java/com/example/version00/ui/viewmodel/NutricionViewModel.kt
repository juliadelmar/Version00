package com.example.version00.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.version00.ui.model.Meal
import com.example.version00.ui.model.MealResponse
import com.example.version00.ui.model.MealUIModel
import com.example.version00.ui.model.toUIModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Retrofit API interface
interface NutritionApiService {

    // Buscar recetas por ingrediente
    @GET("filter.php")
    suspend fun buscarPorIngrediente(@Query("i") ingrediente: String): MealResponse

    companion object {
        private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

        fun create(): NutritionApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(NutritionApiService::class.java)
        }
    }
}

// ViewModel que usa el servicio para cargar recetas
class NutritionViewModel(private val repository: NutritionApiService) : ViewModel() {
    private val _meals = MutableStateFlow<List<MealUIModel>>(emptyList())
    val meals: StateFlow<List<MealUIModel>> = _meals

    fun loadMeals(query: String = "") {
        viewModelScope.launch {
            try {
                val response = repository.buscarPorIngrediente(query)
                _meals.value = response.meals?.map { it.toUIModel() } ?: emptyList()
            } catch (e: Exception) {
                _meals.value = emptyList()
            }
        }
    }
}

// Factory para crear el ViewModel con el servicio inyectado
class NutritionViewModelFactory(
    private val apiService: NutritionApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NutritionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NutritionViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
