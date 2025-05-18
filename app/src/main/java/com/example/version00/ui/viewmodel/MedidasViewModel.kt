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

class MedidasViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var userId: String? = auth.currentUser?.uid

    private val formatoFechaEntrada = DateTimeFormatter.ofPattern("dd MMM yyyy")

    // --- Historial de Peso ---
    private val _historialPeso = MutableStateFlow<List<RegistroPeso>>(emptyList())
    val historialPeso: StateFlow<List<RegistroPeso>> = _historialPeso.asStateFlow()
    val ultimoRegistroPeso: RegistroPeso? get() = _historialPeso.value.lastOrNull()
    val diferenciaPeso: Double get() = (ultimoRegistroPeso?.peso ?: 0.0) - (ultimoRegistroPeso?.meta ?: 0.0)
    val ultimaMetaPeso: Double get() = ultimoRegistroPeso?.meta ?: 0.0

    // --- Medidas Detalladas ---
    private val _medidasDetalladas = MutableStateFlow(MedidasDetalladas()) // Inicia con un objeto vacío
    val medidasDetalladas: StateFlow<MedidasDetalladas> = _medidasDetalladas.asStateFlow()

    // --- Datos Avanzados ---
    private val _datosAvanzados = MutableStateFlow(DatosAvanzados()) // Inicia con un objeto vacío
    val datosAvanzados: StateFlow<DatosAvanzados> = _datosAvanzados.asStateFlow()

    // --- Estado General y Errores ---
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var historialPesoListener: ValueEventListener? = null
    private var medidasDetalladasListener: ValueEventListener? = null
    private var datosAvanzadosListener: ValueEventListener? = null


    init {
        auth.addAuthStateListener { firebaseAuth ->
            val newUser = firebaseAuth.currentUser
            if (newUser != null) {
                if (userId != newUser.uid) { // Si el usuario cambió (login/logout/switch)
                    userId = newUser.uid
                    Log.d("MedidasVM", "AuthStateChanged: Nuevo UID: $userId. Iniciando listeners.")
                    iniciarListeners()
                } else if (userId == null) { // Si era null y ahora hay usuario (primer login)
                    userId = newUser.uid
                    Log.d("MedidasVM", "AuthStateChanged: UID establecido: $userId. Iniciando listeners.")
                    iniciarListeners()
                }
            } else {
                Log.d("MedidasVM", "AuthStateChanged: Usuario es null. Deteniendo listeners.")
                detenerListeners()
                userId = null
                // Limpiar datos locales
                _historialPeso.value = emptyList()
                _medidasDetalladas.value = MedidasDetalladas()
                _datosAvanzados.value = DatosAvanzados()
            }
        }
        // Si ya hay un usuario al crear el ViewModel (ej. app ya estaba logueada)
        if (userId != null) {
            Log.d("MedidasVM", "Init: UID ya existe: $userId. Iniciando listeners.")
            iniciarListeners()
        } else {
            Log.d("MedidasVM", "Init: UID es null. No se inician listeners.")
        }
    }

    private fun getBasePath(): String? {
        return userId?.let { "usuarios/$it/medidasCorporales" }
    }

    private fun iniciarListeners() {
        val basePath = getBasePath() ?: run {
            Log.w("MedidasVM", "No se pudo obtener basePath, UID es null.")
            return
        }
        Log.d("MedidasVM", "Iniciando listeners para basePath: $basePath")

        // Detener listeners anteriores si existieran, para evitar duplicados
        detenerListeners()

        // Listener para Historial de Peso
        val historialRef = database.getReference("$basePath/historialPeso")
        historialPesoListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val typeIndicator = object : GenericTypeIndicator<List<@JvmSuppressWildcards RegistroPeso>>() {}
                val lista = snapshot.getValue(typeIndicator)
                _historialPeso.value = lista ?: emptyList()
                Log.d("MedidasVM", "HistorialPeso actualizado: ${_historialPeso.value.size} registros")
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("MedidasVM", "Error HistorialPeso listener", error.toException())
                _errorMessage.value = "Error cargando historial de peso: ${error.message}"
            }
        }.also { historialRef.addValueEventListener(it) }

        // Listener para Medidas Detalladas
        val medidasRef = database.getReference("$basePath/medidasDetalladas")
        medidasDetalladasListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(MedidasDetalladas::class.java)
                _medidasDetalladas.value = data ?: MedidasDetalladas()
                Log.d("MedidasVM", "MedidasDetalladas actualizadas.")
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("MedidasVM", "Error MedidasDetalladas listener", error.toException())
                _errorMessage.value = "Error cargando medidas detalladas: ${error.message}"
            }
        }.also { medidasRef.addValueEventListener(it) }


        // Listener para Datos Avanzados
        val avanzadosRef = database.getReference("$basePath/datosAvanzados")
        datosAvanzadosListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(DatosAvanzados::class.java)
                _datosAvanzados.value = data ?: DatosAvanzados()
                Log.d("MedidasVM", "DatosAvanzados actualizados.")
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("MedidasVM", "Error DatosAvanzados listener", error.toException())
                _errorMessage.value = "Error cargando datos avanzados: ${error.message}"
            }
        }.also { avanzadosRef.addValueEventListener(it) }
    }

    private fun detenerListeners() {
        val basePath = getBasePath()
        Log.d("MedidasVM", "Intentando detener listeners para UID: $userId")

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


    // --- Funciones para modificar datos ---

    fun agregarRegistroPeso(peso: Double, meta: Double, imc: Double?, grasa: Double?) {
        val basePath = getBasePath() ?: return Unit.also { _errorMessage.value = "Usuario no autenticado." }
        val nuevoRegistro = RegistroPeso(
            fecha = LocalDate.now().format(formatoFechaEntrada),
            peso = peso, meta = meta, imc = imc, grasa = grasa
        )
        val nuevaListaHistorial = _historialPeso.value.toMutableList().apply { add(nuevoRegistro) }

        database.getReference("$basePath/historialPeso").setValue(nuevaListaHistorial)
            .addOnSuccessListener { Log.d("MedidasVM", "Registro de peso guardado.") }
            .addOnFailureListener { e ->
                Log.e("MedidasVM", "Error guardando registro de peso", e)
                _errorMessage.value = "Error al guardar peso: ${e.message}"
            }
    }

    fun guardarMedidasDetalladas(nuevasMedidas: MedidasDetalladas) {
        val basePath = getBasePath() ?: return Unit.also { _errorMessage.value = "Usuario no autenticado." }
        database.getReference("$basePath/medidasDetalladas").setValue(nuevasMedidas)
            .addOnSuccessListener { Log.d("MedidasVM", "Medidas detalladas guardadas.") }
            .addOnFailureListener { e ->
                Log.e("MedidasVM", "Error guardando medidas detalladas", e)
                _errorMessage.value = "Error al guardar medidas: ${e.message}"
            }
    }

    fun guardarDatosAvanzados(nuevosDatos: DatosAvanzados) {
        val basePath = getBasePath() ?: return Unit.also { _errorMessage.value = "Usuario no autenticado." }
        database.getReference("$basePath/datosAvanzados").setValue(nuevosDatos)
            .addOnSuccessListener { Log.d("MedidasVM", "Datos avanzados guardados.") }
            .addOnFailureListener { e ->
                Log.e("MedidasVM", "Error guardando datos avanzados", e)
                _errorMessage.value = "Error al guardar datos avanzados: ${e.message}"
            }
    }

    fun limpiarMensajeError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("MedidasVM", "onCleared llamado. Deteniendo listeners.")
        detenerListeners() // Importante para evitar fugas de memoria
    }
}