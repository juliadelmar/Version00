package com.example.version00.ui.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.version00.ui.data.Rutina
import com.example.version00.ui.model.Ejercicio
import com.example.version00.ui.model.EjercicioGuardado
import com.example.version00.ui.network.RetrofitClient
import com.example.version00.ui.screens.EntradaHistorial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class RutinaFirebaseViewModel : ViewModel() {

    private val dbRef = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    private fun getUserUid(): String? = auth.currentUser?.uid

    // Guardar ejercicio detallado
    fun guardarEjercicioDetalladoEnRutina(
        rutinaId: Int,
        ejercicio: EjercicioGuardado,
        onResult: (Boolean, String) -> Unit
    ) {
        val uid = getUserUid()
        if (uid == null) {
            onResult(false, "Usuario no autenticado")
            return
        }

        val ejercicioKey = "ejercicio_${ejercicio.id}"
        val ejercicioRef = dbRef
            .child("usuarios").child(uid)
            .child("rutinas").child("rutina_$rutinaId")
            .child(ejercicioKey)

        ejercicioRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    onResult(false, "⚠️ Ya existe en la rutina")
                } else {
                    ejercicioRef.setValue(ejercicio)
                        .addOnSuccessListener { onResult(true, "✅ Ejercicio guardado") }
                        .addOnFailureListener {
                            Log.e("Firebase", "❌ Error al guardar ejercicio: ${it.message}")
                            onResult(false, "❌ Error al guardar")
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "❌ Cancelado: ${error.message}")
                onResult(false, "❌ Error en la operación")
            }
        })
    }

    // Obtener un ejercicio original (por ID)
    fun obtenerEjercicioPorId(ejercicioId: Int, onResult: (Ejercicio?) -> Unit) {
        val api = RetrofitClient.api
        viewModelScope.launch {
            try {
                val lista = api.obtenerEjercicios()
                val encontrado = lista.find { it._id == ejercicioId }
                onResult(encontrado)
            } catch (e: Exception) {
                Log.e("Firebase", "Error al buscar ejercicio: ${e.message}")
                onResult(null)
            }
        }
    }

    // Obtener los ejercicios guardados de una rutina
    fun obtenerEjerciciosDeRutina(rutinaId: Int, onResult: (List<EjercicioGuardado>) -> Unit) {
        val uid = getUserUid()
        if (uid == null) {
            Log.e("RutinaFirebaseVM", "❌ Usuario no autenticado. No se puede leer.")
            onResult(emptyList())
            return
        }

        val rutinaRef = dbRef.child("usuarios").child(uid)
            .child("rutinas").child("rutina_$rutinaId")

        rutinaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<EjercicioGuardado>()
                snapshot.children.forEach { data ->
                    val ejercicio = data.getValue(EjercicioGuardado::class.java)
                    ejercicio?.let { lista.add(it) }
                }
                Log.d("RutinaFirebaseVM", "✅ Ejercicios cargados: ${lista.size}")
                onResult(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RutinaFirebaseVM", "❌ Error al obtener ejercicios: ${error.message}")
                onResult(emptyList())
            }
        })
    }
    fun obtenerHistorialFatiga(onResult: (List<EntradaHistorial>) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return onResult(emptyList())

        val ref = FirebaseDatabase.getInstance().reference
            .child("usuarios").child(uid).child("historial_fatiga")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<EntradaHistorial>()
                snapshot.children.forEach { item ->
                    val fecha = item.child("fecha").getValue(String::class.java) ?: return@forEach
                    val nombre = item.child("rutinaNombre").getValue(String::class.java) ?: "Sin nombre"
                    val fatiga = item.child("fatigaPorMusculo").value as? Map<*, *>

                    val musculos = fatiga?.keys?.joinToString(", ") ?: "Sin datos"
                    lista.add(EntradaHistorial(fecha, nombre, musculos))
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
        onResult: (Boolean, String) -> Unit
    ) {
        val uid = getUserUid()
        if (uid == null) {
            onResult(false, "Usuario no autenticado")
            return
        }

        // Fecha legible como string (puedes usar timestamp si prefieres)
        val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val historial = mapOf(
            "rutinaId" to rutinaId,
            "rutinaNombre" to rutinaNombre,
            "fecha" to fecha,
            "fatigaPorMusculo" to fatigaPorMusculo
        )

        val historialRef = dbRef
            .child("usuarios")
            .child(uid)
            .child("historial_fatiga")
            .push()

        historialRef.setValue(historial)
            .addOnSuccessListener {
                Log.d(TAG, "✅ Historial de fatiga guardado para rutina $rutinaId")
                onResult(true, "✅ Historial de fatiga guardado correctamente")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Error al guardar historial de fatiga: ${e.message}", e)
                onResult(false, "❌ Error al guardar historial: ${e.message}")
            }
    }


    // Eliminar un ejercicio por ID
    fun eliminarEjercicioDeRutina(rutinaId: Int, ejercicioId: Int) {
        val uid = getUserUid()
        if (uid == null) {
            Log.e("Firebase", "❌ Usuario no autenticado. No se puede eliminar.")
            return
        }

        val rutinaRef = dbRef.child("usuarios").child(uid)
            .child("rutinas").child("rutina_$rutinaId")

        rutinaRef.orderByChild("id").equalTo(ejercicioId.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        child.ref.removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error al eliminar ejercicio: ${error.message}")
                }
            })
    }
    fun obtenerDiasConEjercicio(onResult: (List<LocalDate>) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return onResult(emptyList())
        val dbRef = FirebaseDatabase.getInstance().reference
            .child("usuarios").child(uid).child("historial_fatiga")

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fechas = snapshot.children.mapNotNull { data ->
                    val fechaStr = data.child("fecha").getValue(String::class.java)
                    try {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val dateTime = java.time.LocalDateTime.parse(fechaStr, formatter)
                        dateTime.toLocalDate()
                    } catch (e: Exception) {
                        null
                    }
                }
                onResult(fechas)
                Log.d("Firebase", "Fechas parseadas: $fechas")

            }

            override fun onCancelled(error: DatabaseError) {
                onResult(emptyList())
            }
        })
    }




    // Obtener un ejercicio guardado específico
    fun obtenerEjercicioGuardadoDeRutina(
        rutinaId: Int,
        ejercicioId: Int,
        onResult: (EjercicioGuardado?) -> Unit
    ) {
        val uid = getUserUid()
        if (uid == null) {
            Log.e("Firebase", "Usuario no autenticado al obtener ejercicio guardado.")
            onResult(null)
            return
        }

        val ejercicioRef = dbRef
            .child("usuarios").child(uid)
            .child("rutinas").child("rutina_$rutinaId")
            .child("ejercicio_$ejercicioId")

        ejercicioRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ejercicio = snapshot.getValue(EjercicioGuardado::class.java)
                if (ejercicio != null) {
                    Log.d("Firebase", "Ejercicio guardado obtenido: ${ejercicio.nombre}")
                } else {
                    Log.w("Firebase", "No se encontró el ejercicio con ID: $ejercicioId en rutina $rutinaId")
                }
                onResult(ejercicio)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al obtener ejercicio guardado: ${error.message}")
                onResult(null)
            }
        })
    }

    // Actualizar ejercicio guardado
    fun actualizarEjercicioDetalladoEnRutina(
        rutinaId: Int,
        ejercicioActualizado: EjercicioGuardado,
        onResult: (Boolean, String) -> Unit
    ) {
        val uid = getUserUid()
        if (uid == null) {
            onResult(false, "Usuario no autenticado")
            return
        }

        val ejercicioRef = dbRef
            .child("usuarios").child(uid)
            .child("rutinas").child("rutina_$rutinaId")
            .child("ejercicio_${ejercicioActualizado.id}")

        ejercicioRef.setValue(ejercicioActualizado)
            .addOnSuccessListener { onResult(true, "✅ Ejercicio actualizado") }
            .addOnFailureListener {
                Log.e("Firebase", "❌ Error al actualizar ejercicio: ${it.message}")
                onResult(false, "❌ Error al actualizar")
            }
    }

    // NUEVO: Obtener datos de la rutina (por ejemplo, su nombre)
    fun obtenerRutinaPorId(rutinaId: Int, onResult: (Rutina?) -> Unit) {
        val uid = getUserUid()
        if (uid == null) {
            onResult(null)
            return
        }

        val rutinaRef = dbRef
            .child("usuarios").child(uid)
            .child("rutinas_metadata").child("rutina_$rutinaId")

        rutinaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val rutina = snapshot.getValue(Rutina::class.java)
                if (rutina != null) {
                    Log.d("Firebase", "✅ Rutina obtenida: ${rutina.nombre}")
                } else {
                    Log.w("Firebase", "⚠️ No se encontró la rutina con ID: $rutinaId")
                }
                onResult(rutina)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "❌ Error al obtener rutina: ${error.message}")
                onResult(null)
            }
        })
    }
}
