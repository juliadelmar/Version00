package com.example.version00.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.version00.ui.model.Ejercicio
import com.example.version00.ui.model.EjercicioGuardado
import com.example.version00.ui.model.SerieEjercicio
import com.example.version00.ui.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.launch


class RutinaFirebaseViewModel : ViewModel() {

    private val dbRef = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    private fun getUserUid(): String? {
        return auth.currentUser?.uid
    }

    fun guardarEjercicioDetalladoEnRutina(
        rutinaId: Int,
        ejercicio: EjercicioGuardado,
        onResult: (Boolean, String) -> Unit // Boolean: success, String: message
    ) {
        val uid = getUserUid()
        if (uid == null) {
            onResult(false, "Usuario no autenticado")
            return
        }

        // Usar el ID del ejercicio como parte de la clave
        // Esto previene duplicados por diseño y hace la comprobación más rápida
        val ejercicioKey = "ejercicio_${ejercicio.id}"
        val ejercicioRef = dbRef
            .child("usuarios")
            .child(uid)
            .child("rutinas")
            .child("rutina_$rutinaId")
            .child(ejercicioKey) // Ruta directa al ejercicio por su ID

        ejercicioRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Ya existe, devolver false
                    onResult(false, "⚠️ Ya existe en la rutina")
                } else {
                    // No existe, guardar
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


    fun obtenerEjerciciosDeRutina(rutinaId: Int, onResult: (List<EjercicioGuardado>) -> Unit) {
        val uid = getUserUid()
        if (uid == null) {
            Log.e("RutinaFirebaseVM", "❌ Usuario no autenticado. No se puede leer.")
            onResult(emptyList())
            return
        }

        val rutinaRef = dbRef
            .child("usuarios")
            .child(uid)
            .child("rutinas")
            .child("rutina_$rutinaId") // Aquí no cambia, ya que leeremos todos los hijos

        rutinaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<EjercicioGuardado>()
                snapshot.children.forEach { data -> // data.key será "ejercicio_123"
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
    fun eliminarEjercicioDeRutina(rutinaId: Int, ejercicioId: Int) {
        val uid = getUserUid()
        if (uid == null) {
            Log.e("Firebase", "❌ Usuario no autenticado. No se puede eliminar.")
            return
        }

        val rutinaRef = dbRef.child("usuarios")
            .child(uid)
            .child("rutinas")
            .child("rutina_$rutinaId")

        rutinaRef.orderByChild("id").equalTo(ejercicioId.toDouble()).addListenerForSingleValueEvent(object : ValueEventListener {
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
    // En RutinaFirebaseViewModel.kt

// ... (código existente) ...

    fun obtenerEjercicioGuardadoDeRutina(
        rutinaId: Int,
        ejercicioId: Int, // Este es el _id del ejercicio original, usado como 'id' en EjercicioGuardado
        onResult: (EjercicioGuardado?) -> Unit
    ) {
        val uid = getUserUid()
        if (uid == null) {
            Log.e("Firebase", "Usuario no autenticado al obtener ejercicio guardado.")
            onResult(null)
            return
        }
        val ejercicioRef = dbRef
            .child("usuarios")
            .child(uid)
            .child("rutinas")
            .child("rutina_$rutinaId")
            .child("ejercicio_$ejercicioId") // Clave directa del ejercicio guardado

        ejercicioRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ejercicio = snapshot.getValue(EjercicioGuardado::class.java)
                if (ejercicio != null) {
                    Log.d("Firebase", "Ejercicio guardado obtenido: ${ejercicio.nombre}")
                } else {
                    Log.w("Firebase", "No se encontró el ejercicio guardado con id: $ejercicioId en rutina $rutinaId")
                }
                onResult(ejercicio)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al obtener ejercicio guardado: ${error.message}")
                onResult(null)
            }
        })
    }

    fun actualizarEjercicioDetalladoEnRutina(
        rutinaId: Int,
        ejercicioActualizado: EjercicioGuardado,
        onResult: (Boolean, String) -> Unit // Boolean: success, String: message
    ) {
        val uid = getUserUid()
        if (uid == null) {
            onResult(false, "Usuario no autenticado")
            return
        }
        // La clave del ejercicio es ejercicio_${ejercicioActualizado.id}
        val ejercicioRef = dbRef
            .child("usuarios")
            .child(uid)
            .child("rutinas")
            .child("rutina_$rutinaId")
            .child("ejercicio_${ejercicioActualizado.id}")

        ejercicioRef.setValue(ejercicioActualizado)
            .addOnSuccessListener { onResult(true, "✅ Ejercicio actualizado") }
            .addOnFailureListener {
                Log.e("Firebase", "❌ Error al actualizar ejercicio: ${it.message}")
                onResult(false, "❌ Error al actualizar")
            }
    }

// ... (resto del ViewModel) ...

}