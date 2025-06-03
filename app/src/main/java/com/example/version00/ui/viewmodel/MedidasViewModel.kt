package com.example.version00.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.version00.ui.data.DatosAvanzados
import com.example.version00.ui.data.MedidasDetalladas
import com.example.version00.ui.data.RegistroPeso
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
// Clase de ViewModel para manejar la lógica de medidas corporales
class MedidasViewModel : ViewModel() {
    // Instancia de FirebaseAuth para autenticar usuarios
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    // Instancia de FirebaseDatabase para interactuar con la base de datos
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    // UID del usuario actual
    private var userId: String? = auth.currentUser?.uid

    // Formato de fecha para la entrada de datos
    private val formatoFechaEntrada = DateTimeFormatter.ofPattern("dd MMM yyyy")

    // Estado de los datos de medidas corporales
    private val _historialPeso = MutableStateFlow<List<RegistroPeso>>(emptyList())
    val historialPeso: StateFlow<List<RegistroPeso>> = _historialPeso.asStateFlow()
    // Último registro de peso
    val ultimoRegistroPeso: RegistroPeso? get() = _historialPeso.value.lastOrNull()
    // Diferencia entre el peso actual y la meta
    val diferenciaPeso: Double get() = (ultimoRegistroPeso?.peso ?: 0.0) - (ultimoRegistroPeso?.meta ?: 0.0)
    // Última meta de peso
    val ultimaMetaPeso: Double get() = ultimoRegistroPeso?.meta ?: 0.0

    // Estado de las medidas detalladas
    private val _medidasDetalladas = MutableStateFlow(MedidasDetalladas())
    val medidasDetalladas: StateFlow<MedidasDetalladas> = _medidasDetalladas.asStateFlow()

    // Estado de los datos avanzados
    private val _datosAvanzados = MutableStateFlow(DatosAvanzados())
    val datosAvanzados: StateFlow<DatosAvanzados> = _datosAvanzados.asStateFlow()

    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Listeners para los datos de medidas corporales
    private var historialPesoListener: ValueEventListener? = null
    private var medidasDetalladasListener: ValueEventListener? = null
    private var datosAvanzadosListener: ValueEventListener? = null

    // Inicialización del ViewModel
    init {
        // Escucha los cambios en el estado de autenticación
        auth.addAuthStateListener { firebaseAuth ->
            // Obtiene el usuario actual
            val newUser = firebaseAuth.currentUser
            if (newUser != null) {
                // Si el usuario cambió, actualiza el UID y reinicia los listeners
                if (userId != newUser.uid) {
                    userId = newUser.uid
                    Log.d("MedidasVM", "AuthStateChanged: Nuevo UID: $userId. Iniciando listeners.")
                    iniciarListeners()
                }
            } else {
                // Si el usuario se desconecta, detiene los listeners y limpia los datos
                Log.d("MedidasVM", "AuthStateChanged: Usuario es null. Deteniendo listeners.")
                detenerListeners()
                userId = null
                _historialPeso.value = emptyList()
                _medidasDetalladas.value = MedidasDetalladas()
                _datosAvanzados.value = DatosAvanzados()
            }
        }

        // Si ya hay un usuario autenticado, inicia los listeners
        if (userId != null) {
            Log.d("MedidasVM", "Init: UID ya existe: $userId. Iniciando listeners.")
            iniciarListeners()
        } else {
            Log.d("MedidasVM", "Init: UID es null. No se inician listeners.")
        }
    }

    // Función para obtener la ruta base para los datos de medidas corporales
    private fun getBasePath(): String? {
        return userId?.let { "usuarios/$it/medidasCorporales" }
    }

    // Función para iniciar los listeners para los datos de medidas corporales
    private fun iniciarListeners() {
        // Obtiene la ruta base para los datos de medidas corporales
        val basePath = getBasePath() ?: return

        // Detiene los listeners anteriores si existieran
        detenerListeners()

        // Inicia el listener para el historial de peso
        val historialRef = database.getReference("$basePath/historialPeso")
        historialPesoListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Actualiza el estado del historial de peso
                val typeIndicator = object : GenericTypeIndicator<List<@JvmSuppressWildcards RegistroPeso>>() {}
                val lista = snapshot.getValue(typeIndicator)
                _historialPeso.value = lista ?: emptyList()
                Log.d("MedidasVM", "HistorialPeso actualizado: ${_historialPeso.value.size} registros")
            }

            override fun onCancelled(error: DatabaseError) {
                // Loguea el error y actualiza el estado de error
                Log.e("MedidasVM", "Error HistorialPeso listener", error.toException())
                _errorMessage.value = "Error cargando historial de peso: ${error.message}"
            }
        }.also { historialRef.addValueEventListener(it) }

 // Listener para Medidas Detalladas
val medidasRef = database.getReference("$basePath/medidasDetalladas")
medidasDetalladasListener = object : ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) {
        // Obtiene el valor de las medidas detalladas desde la base de datos
        val data = snapshot.getValue(MedidasDetalladas::class.java)
        // Actualiza el estado de las medidas detalladas
        _medidasDetalladas.value = data ?: MedidasDetalladas()
        Log.d("MedidasVM", "MedidasDetalladas actualizadas.")
    }

    override fun onCancelled(error: DatabaseError) {
        // Loguea el error y actualiza el estado de error
        Log.e("MedidasVM", "Error MedidasDetalladas listener", error.toException())
        _errorMessage.value = "Error cargando medidas detalladas: ${error.message}"
    }
}.also { medidasRef.addValueEventListener(it) }

// Listener para Datos Avanzados
val avanzadosRef = database.getReference("$basePath/datosAvanzados")
datosAvanzadosListener = object : ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) {
        // Obtiene el valor de los datos avanzados desde la base de datos
        val data = snapshot.getValue(DatosAvanzados::class.java)
        // Actualiza el estado de los datos avanzados
        _datosAvanzados.value = data ?: DatosAvanzados()
        Log.d("MedidasVM", "DatosAvanzados actualizados.")
    }

    override fun onCancelled(error: DatabaseError) {
        // Loguea el error y actualiza el estado de error
        Log.e("MedidasVM", "Error DatosAvanzados listener", error.toException())
        _errorMessage.value = "Error cargando datos avanzados: ${error.message}"
    }
}.also { avanzadosRef.addValueEventListener(it) }

// Función para detener los listeners
private fun detenerListeners() {
    // Obtiene la ruta base para los datos de medidas corporales
    val basePath = getBasePath()

    // Detiene los listeners para los datos de medidas corporales
    historialPesoListener?.let { listener ->
        basePath?.let { database.getReference("$it/historialPeso").removeEventListener(listener) }
        Log.d("MedidasVM", "Listener de HistorialPeso detenido.")
    }
    historialPesoListener = null

    medidasDetalladasListener?.let { listener ->
        basePath?.let { database.getReference("$it/medidasDetalladas").removeEventListener(listener) }
        Log.d("MedidasVM", "Listener de MedidasDetalladas detenido.")
    }
    medidasDetalladasListener = null

    datosAvanzadosListener?.let { listener ->
        basePath?.let { database.getReference("$it/datosAvanzados").removeEventListener(listener) }
        Log.d("MedidasVM", "Listener de DatosAvanzados detenido.")
    }
    datosAvanzadosListener = null
}

// Funciones para modificar los datos de medidas corporales
// ...

// Función para agregar un registro de peso
fun agregarRegistroPeso(peso: Double, meta: Double, imc: Double?, grasa: Double?) {
    // Obtiene la ruta base para los datos de medidas corporales
    val basePath = getBasePath() ?: return

    // Crea un nuevo registro de peso
    val nuevoRegistro = RegistroPeso(
        fecha = LocalDate.now().format(formatoFechaEntrada),
        peso = peso,
        meta = meta,
        imc = imc,
        grasa = grasa
    )

    // Actualiza la lista de registros de peso
    val nuevaListaHistorial = _historialPeso.value.toMutableList().apply { add(nuevoRegistro) }

    // Guarda la lista de registros de peso en la base de datos
    database.getReference("$basePath/historialPeso").setValue(nuevaListaHistorial)
        .addOnSuccessListener { Log.d("MedidasVM", "Registro de peso guardado.") }
        .addOnFailureListener { e ->
            Log.e("MedidasVM", "Error guardando registro de peso", e)
            _errorMessage.value = "Error al guardar peso: ${e.message}"
        }
}

// Función para guardar las medidas detalladas
fun guardarMedidasDetalladas(nuevasMedidas: MedidasDetalladas) {
    // Obtiene la ruta base para los datos de medidas corporales
    val basePath = getBasePath() ?: return

    // Guarda las medidas detalladas en la base de datos
    database.getReference("$basePath/medidasDetalladas").setValue(nuevasMedidas)
        .addOnSuccessListener { Log.d("MedidasVM", "Medidas detalladas guardadas.") }
        .addOnFailureListener { e ->
            Log.e("MedidasVM", "Error guardando medidas detalladas", e)
            _errorMessage.value = "Error al guardar medidas: ${e.message}"
        }
}

// Función para guardar los datos avanzados
fun guardarDatosAvanzados(nuevosDatos: DatosAvanzados) {
    // Obtiene la ruta base para los datos de medidas corporales
    val basePath = getBasePath() ?: return

    // Guarda los datos avanzados en la base de datos
    database.getReference("$basePath/datosAvanzados").setValue(nuevosDatos)
        .addOnSuccessListener { Log.d("MedidasVM", "Datos avanzados guardados.") }
        .addOnFailureListener { e ->
            Log.e("MedidasVM", "Error guardando datos avanzados", e)
            _errorMessage.value = "Error al guardar datos avanzados: ${e.message}"
        }
}

// Función para limpiar el mensaje de error
fun limpiarMensajeError() {
    _errorMessage.value = null
}

// Función para detener los listeners cuando el ViewModel se destruye
override fun onCleared() {
    super.onCleared()
    Log.d("MedidasVM", "onCleared llamado. Deteniendo listeners.")
    detenerListeners()
}
