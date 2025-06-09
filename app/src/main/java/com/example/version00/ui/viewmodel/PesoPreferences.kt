package com.example.version00.ui.viewmodel

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("peso_preferences")
val Context.usuarioDataStore by preferencesDataStore("usuario_preferences")

class PesoPreferences(private val context: Context) {

    companion object {
        val PESO_KEY = doublePreferencesKey("peso_actual")
    }

    val pesoActual: Flow<Double> = context.dataStore.data
        .map { it[PESO_KEY] ?: 0.0 }

    suspend fun guardarPeso(peso: Double) {
        context.dataStore.edit { prefs ->
            prefs[PESO_KEY] = peso
        }
    }
}

class UsuarioPreferences(private val context: Context) {

    companion object {
        private val ALTURA_KEY = doublePreferencesKey("altura_cm")
        private val EDAD_KEY = intPreferencesKey("edad")
    }

    val altura: Flow<Double> = context.usuarioDataStore.data
        .map { it[ALTURA_KEY] ?: 0.0 }

    val edad: Flow<Int> = context.usuarioDataStore.data
        .map { it[EDAD_KEY] ?: 0 }

    suspend fun guardarAlturaYEdad(alturaCm: Double, edadInt: Int) {
        context.usuarioDataStore.edit { prefs ->
            prefs[ALTURA_KEY] = alturaCm
            prefs[EDAD_KEY] = edadInt
        }
    }
}
