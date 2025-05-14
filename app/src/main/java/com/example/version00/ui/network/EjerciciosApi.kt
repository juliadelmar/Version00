package com.example.version00.ui.network
import com.example.version00.ui.model.Ejercicio
import retrofit2.http.GET

interface EjerciciosApi {
    @GET("api/ejercicios/raw")
    suspend fun obtenerEjercicios(): List<Ejercicio>
}
