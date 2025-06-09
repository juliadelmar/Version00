package com.example.version00.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

class HistorialViewModel : ViewModel() {
    private val db = FirebaseDatabase.getInstance().reference
    fun obtenerRutinasPorFecha(
        fecha: LocalDate,
        onResult: (List<RutinaResumen>) -> Unit
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return onResult(emptyList())
        val ref = FirebaseDatabase.getInstance().reference
            .child("usuarios").child(uid).child("rutinasHistorial")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<RutinaResumen>()
                val fechaStr = fecha.toString() // formato yyyy-MM-dd

                snapshot.children.forEach { item ->
                    val fechaItem = item.child("fecha").getValue(String::class.java) ?: return@forEach
                    if (!fechaItem.startsWith(fechaStr)) return@forEach

                    val nombre = item.child("rutinaNombre").getValue(String::class.java) ?: "Sin nombre"
                    val ejercicios = mutableListOf<EjercicioResumen>()

                    val ejerciciosSnap = item.child("ejercicios")
                    ejerciciosSnap.children.forEach { ej ->
                        val nombreEj = ej.child("nombre").getValue(String::class.java) ?: "Ejercicio"
                        val reps = ej.child("reps").children.mapNotNull { it.getValue(String::class.java) }
                        val pesos = ej.child("pesos").children.mapNotNull { it.getValue(String::class.java) }

                        ejercicios.add(EjercicioResumen(nombreEj, reps, pesos))
                    }

                    lista.add(RutinaResumen(nombre, ejercicios))
                }

                onResult(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(emptyList())
            }
        })
    }

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
data class RutinaResumen(
    val nombre: String,
    val ejercicios: List<EjercicioResumen>
)

data class EjercicioResumen(
    val nombre: String,
    val reps: List<String>,
    val pesos: List<String>
)
