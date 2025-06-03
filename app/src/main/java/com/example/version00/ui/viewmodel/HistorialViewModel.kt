package com.example.version00.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Clase de ViewModel para manejar la lógica del historial de fatiga
class HistorialViewModel : ViewModel() {
    // Referencia a la base de datos de Firebase Realtime Database
    private val db = FirebaseDatabase.getInstance().reference

    // Función para guardar el historial de fatiga
    fun guardarHistorialFatiga(
        rutinaId: Int,
        rutinaNombre: String,
        fatigaPorMusculo: Map<String, Double>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Obtiene la fecha y hora actual
        val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // Crea un mapa con los datos del historial de fatiga
        val historial = mapOf(
            "rutinaId" to rutinaId,
            "rutinaNombre" to rutinaNombre,
            "fecha" to fecha,
            "fatigaPorMusculo" to fatigaPorMusculo
        )

        // Genera un ID único para el historial de fatiga
        val key = db.child("historial_fatiga").push().key
        if (key != null) {
            // Guarda el historial de fatiga en la base de datos
            db.child("historial_fatiga").child(key).setValue(historial)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure(it) }
        } else {
            // Llama a la función onFailure si no se pudo generar un ID único
            onFailure(Exception("No se pudo generar clave para historial"))
        }
    }
}
