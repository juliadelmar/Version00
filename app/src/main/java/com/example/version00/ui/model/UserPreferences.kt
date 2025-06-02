// UserPreferences.kt
package com.example.version00.ui.model

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión de Context para crear un DataStore de tipo Preferences
private val Context.dataStore by preferencesDataStore("user_preferences")

/**
 * Clase que encapsula la lógica de acceso a DataStore para guardar y leer
 * la preferencia de tema visual (claro, oscuro o sistema).
 *
 * @param context Contexto de la aplicación.
 */
class UserPreferences(private val context: Context) {

    companion object {
        // Clave usada para guardar la preferencia de tema
        val THEME_KEY = stringPreferencesKey("theme_preference")
    }

    /**
     * Flujo observable que emite el valor actual del tema guardado.
     * Puede ser "light", "dark" o "system".
     */
    val themeFlow: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[THEME_KEY] ?: "system" }

    /**
     * Guarda de forma asíncrona una nueva preferencia de tema en el DataStore.
     *
     * @param theme Cadena que representa el tema ("light", "dark", "system").
     */
    suspend fun saveThemePreference(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }
}
