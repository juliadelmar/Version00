
package com.example.version00.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.version00.ui.data.DatosAvanzados
import com.example.version00.ui.data.MedidasDetalladas
import com.example.version00.ui.data.RegistroPeso

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MedidasViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var userId: String? = auth.currentUser?.uid
    private var pesoPrefs: PesoPreferences? = null
    private var usuarioPrefs: UsuarioPreferences? = null

    private val formatoFechaEntrada = DateTimeFormatter.ofPattern("dd MMM yyyy")

    private val _historialPeso = MutableStateFlow<List<RegistroPeso>>(emptyList())
    val historialPeso: StateFlow<List<RegistroPeso>> = _historialPeso.asStateFlow()
    val ultimoRegistroPeso: RegistroPeso? get() = _historialPeso.value.lastOrNull()
    val diferenciaPeso: Double get() = (ultimoRegistroPeso?.peso ?: 0.0) - (ultimoRegistroPeso?.meta ?: 0.0)
    val ultimaMetaPeso: Double get() = ultimoRegistroPeso?.meta ?: 0.0

    private val _medidasDetalladas = MutableStateFlow(MedidasDetalladas())
    val medidasDetalladas: StateFlow<MedidasDetalladas> = _medidasDetalladas.asStateFlow()

    private val _datosAvanzados = MutableStateFlow(DatosAvanzados())
    val datosAvanzados: StateFlow<DatosAvanzados> = _datosAvanzados.asStateFlow()

    private val _pesoLocal = MutableStateFlow(0.0)
    val pesoLocal: StateFlow<Double> = _pesoLocal.asStateFlow()

    private val _altura = MutableStateFlow("")
    val altura: StateFlow<String> = _altura.asStateFlow()

    private val _edad = MutableStateFlow("")
    val edad: StateFlow<String> = _edad.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var historialPesoListener: ValueEventListener? = null
    private var medidasDetalladasListener: ValueEventListener? = null
    private var datosAvanzadosListener: ValueEventListener? = null

    fun inicializar(context: Context) {
        pesoPrefs = PesoPreferences(context)
        usuarioPrefs = UsuarioPreferences(context)

        viewModelScope.launch {
            pesoPrefs?.pesoActual?.collect {
                _pesoLocal.value = it
            }
        }

        viewModelScope.launch {
            usuarioPrefs?.altura?.collect {
                _altura.value = it.toString()
            }
        }

        viewModelScope.launch {
            usuarioPrefs?.edad?.collect {
                _edad.value = it.toString()
            }
        }
    }

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val newUser = firebaseAuth.currentUser
            if (newUser != null) {
                if (userId != newUser.uid) {
                    userId = newUser.uid
                    iniciarListeners()
                } else if (userId == null) {
                    userId = newUser.uid
                    iniciarListeners()
                }
            } else {
                detenerListeners()
                userId = null
                _historialPeso.value = emptyList()
                _medidasDetalladas.value = MedidasDetalladas()
                _datosAvanzados.value = DatosAvanzados()
            }
        }
        if (userId != null) iniciarListeners()
    }

    private fun getBasePath(): String? = userId?.let { "usuarios/$it/medidasCorporales" }

    private fun iniciarListeners() {
        val basePath = getBasePath() ?: return detenerListeners()

        detenerListeners()

        val historialRef = database.getReference("$basePath/historialPeso")
        historialPesoListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val typeIndicator = object : GenericTypeIndicator<List<@JvmSuppressWildcards RegistroPeso>>() {}
                val lista = snapshot.getValue(typeIndicator)
                _historialPeso.value = lista ?: emptyList()
            }
            override fun onCancelled(error: DatabaseError) {
                _errorMessage.value = "Error historial de peso: ${error.message}"
            }
        }.also { historialRef.addValueEventListener(it) }

        val medidasRef = database.getReference("$basePath/medidasDetalladas")
        medidasDetalladasListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _medidasDetalladas.value = snapshot.getValue(MedidasDetalladas::class.java) ?: MedidasDetalladas()
            }
            override fun onCancelled(error: DatabaseError) {
                _errorMessage.value = "Error medidas detalladas: ${error.message}"
            }
        }.also { medidasRef.addValueEventListener(it) }

        val avanzadosRef = database.getReference("$basePath/datosAvanzados")
        datosAvanzadosListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _datosAvanzados.value = snapshot.getValue(DatosAvanzados::class.java) ?: DatosAvanzados()
            }
            override fun onCancelled(error: DatabaseError) {
                _errorMessage.value = "Error datos avanzados: ${error.message}"
            }
        }.also { avanzadosRef.addValueEventListener(it) }
    }

    private fun detenerListeners() {
        val basePath = getBasePath() ?: return
        historialPesoListener?.let { database.getReference("$basePath/historialPeso").removeEventListener(it) }
        medidasDetalladasListener?.let { database.getReference("$basePath/medidasDetalladas").removeEventListener(it) }
        datosAvanzadosListener?.let { database.getReference("$basePath/datosAvanzados").removeEventListener(it) }
        historialPesoListener = null
        medidasDetalladasListener = null
        datosAvanzadosListener = null
    }

    fun agregarRegistroPeso(peso: Double, meta: Double, imc: Double?, grasa: Double?) {
        val basePath = getBasePath() ?: return run { _errorMessage.value = "Usuario no autenticado." }

        val alturaActual = _altura.value.toDoubleOrNull()
        val imcCalculado = if (imc != null) imc else {
            if (alturaActual != null && alturaActual > 0) {
                calcularIMC(peso, alturaActual).toDoubleOrNull()
            } else null
        }


        val nuevoRegistro = RegistroPeso(
            fecha = LocalDate.now().format(formatoFechaEntrada),
            peso = peso,
            meta = meta,
            imc = imcCalculado,
            grasa = grasa
        )
        val nuevaLista = _historialPeso.value.toMutableList().apply { add(nuevoRegistro) }

        database.getReference("$basePath/historialPeso").setValue(nuevaLista)
            .addOnSuccessListener {
                viewModelScope.launch {
                    pesoPrefs?.guardarPeso(peso)
                }
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Error al guardar peso: ${e.message}"
            }
    }

    fun guardarMedidasDetalladas(nuevasMedidas: MedidasDetalladas) {
        val basePath = getBasePath() ?: return run { _errorMessage.value = "Usuario no autenticado." }
        database.getReference("$basePath/medidasDetalladas").setValue(nuevasMedidas)
    }

    fun guardarDatosAvanzados(nuevosDatos: DatosAvanzados) {
        val basePath = getBasePath() ?: return run { _errorMessage.value = "Usuario no autenticado." }
        database.getReference("$basePath/datosAvanzados").setValue(nuevosDatos)
    }

    fun guardarAlturaYEdad(altura: Double, edad: Int) {
        viewModelScope.launch {
            usuarioPrefs?.guardarAlturaYEdad(altura,edad)
            _altura.value = altura.toString()
            _edad.value = edad.toString()
        }
    }

    fun limpiarMensajeError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        detenerListeners()
    }
    @SuppressLint("DefaultLocale")
    fun calcularIMC(pesoKg: Double, alturaCm: Double): String {
        if (pesoKg <= 0 || alturaCm <= 0) return "--"
        val alturaM = alturaCm / 100.0
        val imc = pesoKg / (alturaM * alturaM)
        return String.format("%.2f", imc)
    }

}
