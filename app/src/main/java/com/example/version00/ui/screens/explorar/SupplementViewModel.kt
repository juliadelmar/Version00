package com.example.version00.ui.screens.explorar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.version00.ui.screens.explorar.NutritionixApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SupplementViewModel(
    private val apiService: NutritionixApiService,
    private val appId: String,
    private val apiKey: String
) : ViewModel() {

    // Lista combinada de suplementos comunes y de marca (usa tipo Any, podrías crear modelos para ambos)
    private val _supplements = MutableStateFlow<List<Any>>(emptyList())
    val supplements: StateFlow<List<Any>> = _supplements

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadSupplements() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // La llamada pasa los headers requeridos (appId, apiKey)
                val response = apiService.searchSupplements(
                    query = "supplement",
                    appId = "694ca093",                  // Tu x-app-id real
                    apiKey = "cdf3a7d5dba1923d02decfe5e39bdf3f",  // Tu x-app-key real
                    userId = "0"
                )

                // Combinar resultados comunes y de marca en una lista
                val combined = mutableListOf<Any>().apply {
                    addAll(response.common)
                    addAll(response.branded)
                }
                _supplements.value = combined
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class SupplementViewModelFactory(
    private val apiService: NutritionixApiService,
    private val appId: String,
    private val apiKey: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SupplementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SupplementViewModel(apiService, appId, apiKey) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
