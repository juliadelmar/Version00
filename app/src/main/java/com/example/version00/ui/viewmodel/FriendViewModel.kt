package com.example.version00.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val avatarUrl: String = ""
)

data class SolicitudAmistad(
    val uidEmisor: String = "",
    val nombreEmisor: String = "",
    val emailEmisor: String = "",
    val avatarEmisor: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

            "uidEmisor" to usuarioEmisor.uid,
            "nombreEmisor" to usuarioEmisor.nombre,
            "emailEmisor" to usuarioEmisor.email,
// Clase de ViewModel para manejar la lógica de amigos y solicitudes de amistad
class FriendViewModel : ViewModel() {
    // Instancia de FirebaseAuth para autenticar usuarios
    private val auth = FirebaseAuth.getInstance()
    // Referencia a la base de datos de Firebase Realtime Database
    private val db = FirebaseDatabase.getInstance().reference

    // Función para obtener todos los usuarios registrados
    fun obtenerTodosLosUsuarios(onResult: (List<Usuario>) -> Unit) {
        // Obtiene la referencia a la lista de usuarios en la base de datos
        db.child("usuarios")
            .get()
            .addOnSuccessListener { snapshot ->
                // Mapea los datos de los usuarios a una lista de objetos Usuario
                val usuarios = snapshot.children.mapNotNull { child ->
                    val uid = child.key ?: return@mapNotNull null
                    val nombre = child.child("nombre").getValue(String::class.java) ?: ""
                    val email = child.child("email").getValue(String::class.java) ?: ""
                    val avatarUrl = child.child("avatarUrl").getValue(String::class.java) ?: ""
                    Usuario(uid, nombre, email, avatarUrl)
                }
                // Llama a la función onResult con la lista de usuarios
                onResult(usuarios)
            }
            .addOnFailureListener {
                // Loguea el error y llama a la función onResult con una lista vacía
                Log.e("FriendViewModel", "Error al obtener usuarios: ${it.message}")
                onResult(emptyList())
            }
    }

    // Función para enviar una solicitud de amistad
    fun enviarSolicitudAmistad(uidDestino: String, usuarioEmisor: Usuario, onResult: (Boolean) -> Unit) {
        // Genera un ID único para la solicitud de amistad
        val solicitudId = db.child("solicitudes_amistad").child(uidDestino).push().key ?: return

        // Crea un mapa con los datos de la solicitud de amistad
        val solicitud = mapOf(
            "uidEmisor" to usuarioEmisor.uid,
            "nombreEmisor" to usuarioEmisor.nombre,
            "emailEmisor" to usuarioEmisor.email,
            "avatarEmisor" to usuarioEmisor.avatarUrl,
            "timestamp" to ServerValue.TIMESTAMP
        )

        // Guarda la solicitud de amistad en la base de datos
        db.child("solicitudes_amistad").child(uidDestino).child(solicitudId)
            .setValue(solicitud)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener {
                // Loguea el error y llama a la función onResult con false
                Log.e("FriendViewModel", "Error al enviar solicitud: ${it.message}")
                onResult(false)
            }
    }

    // Función para aceptar una solicitud de amistad y agregar amistad bidireccional
    fun aceptarSolicitud(
        uidEmisor: String,
        solicitudId: String,
        datosEmisor: Usuario,
        datosReceptor: Usuario,
        onResult: (Boolean) -> Unit
    ) {
        // Crea un mapa con las actualizaciones necesarias para aceptar la solicitud de amistad
        val updates = mapOf<String, Any?>(
            "usuarios/${datosReceptor.uid}/amigos/$uidEmisor" to datosEmisor,
            "usuarios/$uidEmisor/amigos/${datosReceptor.uid}" to datosReceptor,
            "solicitudes_amistad/${datosReceptor.uid}/$solicitudId" to null
        )

        // Actualiza la base de datos con las actualizaciones necesarias
        db.updateChildren(updates)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener {
                // Loguea el error y llama a la función onResult con false
                Log.e("FriendViewModel", "Error al aceptar solicitud: ${it.message}")
                onResult(false)
            }
    }

    // Función para rechazar una solicitud de amistad
    fun rechazarSolicitud(uidDestino: String, solicitudId: String, onResult: (Boolean) -> Unit) {
        // Elimina la solicitud de amistad de la base de datos
        db.child("solicitudes_amistad").child(uidDestino).child(solicitudId)
            .removeValue()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener {
                // Loguea el error y llama a la función onResult con false
                Log.e("FriendViewModel", "Error al rechazar solicitud: ${it.message}")
                onResult(false)
            }
    }

    // Función para obtener las solicitudes de amistad recibidas
    fun obtenerSolicitudesRecibidas(onResult: (List<Pair<String, SolicitudAmistad>>) -> Unit) {
        // Obtiene el UID del usuario actual
        val uid = auth.currentUser?.uid
        if (uid == null) {
            // Loguea el error y llama a la función onResult con una lista vacía si el usuario no está autenticado
            Log.e("FriendViewModel", "Usuario no autenticado")
            onResult(emptyList())
            return
        }

        // Obtiene la referencia a las solicitudes de amistad recibidas en la base de datos
        db.child("solicitudes_amistad").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Mapea los datos de las solicitudes de amistad a una lista de pares (ID, SolicitudAmistad)
                    val solicitudes = snapshot.children.mapNotNull {
                        val solicitud = it.getValue(SolicitudAmistad::class.java)
                        solicitud?.let { s -> it.key?.let { key -> key to s } }
                    }
                    // Llama a la función onResult con la lista de solicitudes de amistad
                    onResult(solicitudes)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Loguea el error y llama a la función onResult con una lista vacía
                    Log.e("FriendViewModel", "Error al obtener solicitudes: ${error.message}")
                    onResult(emptyList())
                }
            })
    }
}
