package com.example.version00.ui.viewmodel

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.version00.ui.data.Rutina
import com.example.version00.ui.model.Ejercicio
import com.example.version00.ui.model.EjercicioGuardado
import com.example.version00.ui.model.EstadisticasEntrenamiento
import com.example.version00.ui.network.RetrofitClient
import com.example.version00.ui.screens.actividades.EntradaHistorial
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
// Clase de ViewModel para manejar la lógica de rutinas y ejercicios en Firebase
class RutinaFirebaseViewModel : ViewModel() {
    // Referencia a la base de datos de Firebase Realtime Database
    private val dbRef = FirebaseDatabase.getInstance().reference
    // Instancia de FirebaseAuth para autenticar usuarios
    private val auth = FirebaseAuth.getInstance()

    // Función para obtener el UID del usuario actual
    private fun getUserUid(): String? = auth.currentUser?.uid

    // Función para guardar un ejercicio detallado en una rutina
    fun guardarEjercicioDetalladoEnRutina(
        rutinaId: Int,
        ejercicio: EjercicioGuardado,
        onResult: (Boolean, String) -> Unit
    ) {
        // Obtiene el UID del usuario actual
        val uid = getUserUid()
        if (uid == null) {
            // Si el usuario no está autenticado, llama a la función onResult con false y un mensaje de error
            onResult(false, "Usuario no autenticado")
            return
        }

        // Crea una referencia al ejercicio en la base de datos
        val ejercicioKey = "ejercicio_${ejercicio.id}"
        val ejercicioRef = dbRef
            .child("usuarios").child(uid)
            .child("rutinas").child("rutina_$rutinaId")
            .child(ejercicioKey)

        // Verifica si el ejercicio ya existe en la rutina
        ejercicioRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Si el ejercicio ya existe, llama a la función onResult con false y un mensaje de error
                    onResult(false, "Ya existe en la rutina")
                } else {
                    // Si el ejercicio no existe, lo guarda en la base de datos
                    ejercicioRef.setValue(ejercicio)
                        .addOnSuccessListener { onResult(true, "Ejercicio guardado") }
                        .addOnFailureListener {
                            // Loguea el error y llama a la función onResult con false y un mensaje de error
                            Log.e("Firebase", "Error al guardar ejercicio: ${it.message}")
                            onResult(false, "Error al guardar")
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Loguea el error y llama a la función onResult con false y un mensaje de error
                Log.e("Firebase", "Cancelado: ${error.message}")
                onResult(false, "Error en la operación")
            }
        })
    }

    // Función para obtener un ejercicio por su ID
    fun obtenerEjercicioPorId(ejercicioId: Int, onResult: (Ejercicio?) -> Unit) {
        // Obtiene la instancia de la API de Retrofit
        val api = RetrofitClient.api
        // Lanza una corrutina para obtener el ejercicio
        viewModelScope.launch {
            try {
                // Obtiene la lista de ejercicios desde la API
                val lista = api.obtenerEjercicios()
                // Busca el ejercicio por su ID
                val encontrado = lista.find { it._id == ejercicioId }
                // Llama a la función onResult con el ejercicio encontrado o null si no se encontró
                onResult(encontrado)
            } catch (e: Exception) {
                // Loguea el error y llama a la función onResult con null
                Log.e("Firebase", "Error al buscar ejercicio: ${e.message}")
                onResult(null)
            }
        }
    }

    // Función para obtener el historial de fatiga
    fun obtenerHistorialFatiga(onResult: (List<EntradaHistorial>) -> Unit) {
        // Obtiene el UID del usuario actual
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return onResult(emptyList())

        // Crea una referencia al historial de fatiga en la base de datos
        val ref = FirebaseDatabase.getInstance().reference
            .child("usuarios").child(uid).child("historial_fatiga")

        // Obtiene el historial de fatiga
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Mapea los datos del historial de fatiga a una lista de objetos EntradaHistorial
                val lista = mutableListOf<EntradaHistorial>()
                snapshot.children.forEach { item ->
                    val fecha = item.child("fecha").getValue(String::class.java) ?: return@forEach
                    val nombre = item.child("rutinaNombre").getValue(String::class.java) ?: "Sin nombre"
                    val fatiga = item.child("fatigaPorMusculo").value as? Map<*, *>
 
                    val musculos = fatiga?.keys?.joinToString(", ") ?: "Sin datos"
                    lista.add(EntradaHistorial(fecha, nombre, musculos))                }
                // Llama a la función onResult con la lista de entradas del historial de fatiga
                onResult(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                // Llama a la función onResult con una lista vacía
                onResult(emptyList())
            }
        })
    }
}

    fun actualizarFatigaAcumulada(
        fatigaNueva: Map<String, Double>,
        onComplete: (Boolean, String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return onComplete(false, "Usuario no autenticado")
        val ref = FirebaseDatabase.getInstance().reference
            .child("fatiga_muscular").child(uid)
        val ahora = System.currentTimeMillis()

        ref.get().addOnSuccessListener { snapshot ->
            val actualizada = mutableMapOf<String, Any>()

            fatigaNueva.forEach { (musculo, nuevaFatiga) ->
                val anterior = snapshot.child(musculo).child("nivel").getValue(Double::class.java) ?: 0.0
                val ultimaFecha = snapshot.child(musculo).child("ultimaActualizacion").getValue(Long::class.java) ?: ahora

                val horas = (ahora - ultimaFecha) / (1000 * 60 * 60).toDouble()
                val anteriorReducida = anterior * Math.exp(-0.1 * horas)
                val total = anteriorReducida + nuevaFatiga

                actualizada[musculo] = mapOf(
                    "nivel" to total,
                    "ultimaActualizacion" to ahora
                )
            }

            ref.updateChildren(actualizada)
                .addOnSuccessListener { onComplete(true, "Fatiga actualizada correctamente") }
                .addOnFailureListener { e -> onComplete(false, "Error: ${e.message}") }
        }.addOnFailureListener { e ->
            onComplete(false, "Error al leer fatiga previa: ${e.message}")
        }
    }

    fun obtenerFatigaActual(uid: String, onResult: (Map<String, Double>) -> Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("fatiga_muscular").child(uid)
        val ahora = System.currentTimeMillis()
        val k = 0.1

        ref.get().addOnSuccessListener { snapshot ->
            val fatiga = mutableMapOf<String, Double>()
            for (musculoSnapshot in snapshot.children) {
                val nivel = musculoSnapshot.child("nivel").getValue(Double::class.java) ?: continue
                val ultimaActualizacion = musculoSnapshot.child("ultimaActualizacion").getValue(Long::class.java) ?: ahora
                val horasPasadas = (ahora - ultimaActualizacion) / (1000 * 60 * 60).toDouble()
                val reducida = nivel * Math.exp(-k * horasPasadas)
                if (reducida > 0.1) {
                    fatiga[musculoSnapshot.key ?: ""] = reducida
                }
            }
            onResult(fatiga)
        }
    }
    @SuppressLint("SuspiciousIndentation")
    fun obtenerEstadisticasDeRutinas(
        dias: Int = 7,
        onResult: (EstadisticasEntrenamiento) -> Unit
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val ref = FirebaseDatabase.getInstance().reference
            .child("usuarios").child(uid).child("rutinasHistorial")
            ref.get().addOnSuccessListener { snapshot ->
                val ahora = LocalDate.now()
                val fechaLimite = ahora.minusDays(dias.toLong())
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                val resultado = EstadisticasEntrenamiento()

                snapshot.children.forEach { rutinaSnap ->
                    val fecha = rutinaSnap.child("fecha").getValue(String::class.java)?.let {
                        try { LocalDate.parse(it, formatter) } catch (_: Exception) { null }
                    } ?: return@forEach

                    if (fecha.isBefore(fechaLimite)) return@forEach

                    rutinaSnap.child("ejercicios").children.forEach { ej ->
                        resultado.ejercicios++
                        val series = ej.child("series").getValue(Int::class.java) ?: 0
                        resultado.series += series
                        val reps = ej.child("reps").children.mapNotNull { it.getValue(String::class.java)?.toIntOrNull() }
                        val pesos = ej.child("pesos").children.mapNotNull {
                            it.getValue(String::class.java)?.replace(",", ".")?.toFloatOrNull()
                        }

                        resultado.reps += reps.sum()
                        resultado.carga += reps.zip(pesos).fold(0f) { acc, (r, p) -> acc + r * p }


                    }
                }

                resultado.calorias = (resultado.carga * 0.05f).toInt()
                onResult(resultado)
            }
    }
    fun guardarUltimoPesoEjecutado(
        ejercicioId: String,
        peso: Float,
        onResult: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        val uid = getUserUid()
        if (uid == null) {
            onResult(false, "Usuario no autenticado")
            return
        }

        val pesoRef = dbRef
            .child("usuarios").child(uid)
            .child("historial_ejercicios").child(ejercicioId)
            .child("ultimo_peso")

        pesoRef.setValue(peso)
            .addOnSuccessListener {
                onResult(true, "✅ Peso guardado")
            }
            .addOnFailureListener {
                onResult(false, "❌ Error al guardar peso: ${it.message}")
            }
    }
    fun obtenerUltimoPesoEjecutado(
        ejercicioId: String,
        onResult: (Float?) -> Unit
    ) {
        val uid = getUserUid()
        if (uid == null) {
            Log.e("Firebase", "Usuario no autenticado al obtener último peso.")
            onResult(null)
            return
        }

        val pesoRef = dbRef
            .child("usuarios").child(uid)
            .child("historial_ejercicios").child(ejercicioId)
            .child("ultimo_peso")

        pesoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val peso = snapshot.getValue(Float::class.java)
                if (peso != null) {
                    Log.d("Firebase", "✅ Último peso obtenido para $ejercicioId: $peso kg")
                } else {
                    Log.w("Firebase", "⚠️ No hay peso guardado para el ejercicio $ejercicioId")
                }
                onResult(peso)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "❌ Error al obtener último peso: ${error.message}")
                onResult(null)
            }
        })
    }
    fun guardarRutinaEnHistorial(
        rutinaId: Int,
        rutinaNombre: String,
        ejercicios: List<EjercicioGuardado>,
        onResult: (Boolean, String) -> Unit
    ) {
        val uid = getUserUid()
        if (uid == null) {
            onResult(false, "Usuario no autenticado")
            return
        }

        val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val ejerciciosMap = ejercicios.associate { ejercicio ->
            "ejercicio_${ejercicio.id}" to mapOf(
                "nombre" to ejercicio.nombre,
                "series" to (ejercicio.pesos?.size ?: 0),
                "pesos" to (ejercicio.pesos ?: emptyList<String>()),
                "reps" to (ejercicio.reps ?: emptyList<String>()),
                "rir" to (ejercicio.repsRecamara ?: emptyList<String>())
            )
        }

        val historialData = mapOf(
            "fecha" to fecha,
            "rutinaId" to rutinaId,
            "rutinaNombre" to rutinaNombre,
            "ejercicios" to ejerciciosMap
        )

        val historialRef = dbRef
            .child("usuarios")
            .child(uid)
            .child("rutinasHistorial")
            .push()

        historialRef.setValue(historialData)
            .addOnSuccessListener {
                Log.d("Firebase", "✅ Historial de rutina guardado")
                onResult(true, "✅ Rutina guardada en historial")
            }
            .addOnFailureListener {
                Log.e("Firebase", "❌ Error al guardar historial: ${it.message}")
                onResult(false, "❌ Error al guardar historial")
            }
    }
    fun obtenerRutinaHistorial(
        rutinaId: Int,
        onResult: (Map<String, Map<String, List<String>>>) -> Unit
    ) {
        val uid = getUserUid()
        if (uid == null) {
            onResult(emptyMap())
            return
        }

        val historialRef = dbRef
            .child("usuarios")
            .child(uid)
            .child("rutinasHistorial")

        historialRef.orderByChild("rutinaId")
            .equalTo(rutinaId.toDouble()) // Firebase guarda los números como Double
            .limitToLast(1) // solo el más reciente
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val resultado = mutableMapOf<String, Map<String, List<String>>>()

                    for (rutinaSnapshot in snapshot.children) {
                        val ejerciciosSnapshot = rutinaSnapshot.child("ejercicios")
                        for (ejercicioSnap in ejerciciosSnapshot.children) {
                            val nombre = ejercicioSnap.child("nombre").getValue(String::class.java) ?: "SinNombre"
                            val pesos = ejercicioSnap.child("pesos").children.mapNotNull { it.getValue(String::class.java) }
                            val reps = ejercicioSnap.child("reps").children.mapNotNull { it.getValue(String::class.java) }
                            val rir = ejercicioSnap.child("rir").children.mapNotNull { it.getValue(String::class.java) }

                            resultado[ejercicioSnap.key ?: ""] = mapOf(
                                "nombre" to listOf(nombre),
                                "pesos" to pesos,
                                "reps" to reps,
                                "rir" to rir
                            )
                        }
                    }

                    onResult(resultado)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "❌ Error al obtener rutina historial: ${error.message}")
                    onResult(emptyMap())
                }
            })
    }
    fun obtenerRutinas(onResult: (List<Rutina>) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return onResult(emptyList())

        FirebaseDatabase.getInstance().reference
            .child("usuarios").child(uid).child("rutinas")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val rutinas = mutableListOf<Rutina>()

                    snapshot.children.forEach { data ->
                        val nombreNodo = data.key ?: return@forEach
                        val id = nombreNodo.removePrefix("rutina_").toIntOrNull() ?: return@forEach
                        val nombre = data.child("nombre").getValue(String::class.java) ?: "Rutina $id"

                        rutinas.add(Rutina(id = id, nombre = nombre))
                    }

                    onResult(rutinas)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }


    fun crearNuevaRutina(nombre: String, onResult: (Int?) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return onResult(null)

        val ref = FirebaseDatabase.getInstance().reference
        val nuevaId = System.currentTimeMillis().toInt()

        val rutinaRef = ref.child("usuarios").child(uid).child("rutinas").child("rutina_$nuevaId")

        val datosIniciales = mapOf(
            "nombre" to nombre
        )

        rutinaRef.setValue(datosIniciales)
            .addOnSuccessListener { onResult(nuevaId) }
            .addOnFailureListener { onResult(null) }
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
    }fun editarNombreRutina(
        rutinaId: Int,
        nuevoNombre: String,
        onResult: (Boolean) -> Unit
    ) {
        val uid = getUserUid()

        val rutinaRef = uid?.let {
        dbRef.child("usuarios").child(it)
            .child("rutinas") .child(rutinaId.toString())
            .child("nombre")
    }


        rutinaRef!!.setValue(nuevoNombre)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun obtenerEjerciciosDeRutina(
        rutinaId: Int,
        callback: (List<EjercicioGuardado>) -> Unit
    ) {
        val uid = getUserUid()

        val rutinaRef = uid?.let {
            dbRef.child("usuarios").child(it)
                .child("rutinas").child("rutina_$rutinaId")
        }
        rutinaRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = snapshot.children.mapNotNull {
                    try {
                        it.getValue(EjercicioGuardado::class.java)
                    } catch (e: Exception) {
                        null // Ignora nodos mal formados
                    }
                }
                callback(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
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
