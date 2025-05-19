package com.example.version00.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistorialViewModel : ViewModel() {
    private val db = FirebaseDatabase.getInstance().reference

    fun guardarHistorialFatiga(
        rutinaId: Int,
        rutinaNombre: String,
        fatigaPorMusculo: Map<String, Double>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val historial = mapOf(
            "rutinaId" to rutinaId,
            "rutinaNombre" to rutinaNombre,
            "fecha" to fecha,
            "fatigaPorMusculo" to fatigaPorMusculo
        )

        val key = db.child("historial_fatiga").push().key
        if (key != null) {
            db.child("historial_fatiga").child(key).setValue(historial)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure(it) }
        } else {
            onFailure(Exception("No se pudo generar clave para historial"))
        }
    }
}
