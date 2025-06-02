package com.example.version00.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// --- Modelos de datos ---
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

// --- ViewModel ---
class FriendViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    // 🔍 Obtener todos los usuarios registrados
    fun obtenerTodosLosUsuarios(onResult: (List<Usuario>) -> Unit) {
        db.child("usuarios")
            .get()
            .addOnSuccessListener { snapshot ->
                val usuarios = snapshot.children.mapNotNull { child ->
                    val uid = child.key ?: return@mapNotNull null
                    val nombre = child.child("nombre").getValue(String::class.java) ?: ""
                    val email = child.child("email").getValue(String::class.java) ?: ""
                    val avatarUrl = child.child("avatarUrl").getValue(String::class.java) ?: ""
                    Usuario(uid, nombre, email, avatarUrl)
                }
                onResult(usuarios)
            }
            .addOnFailureListener {
                Log.e("FriendViewModel", "Error al obtener usuarios: ${it.message}")
                onResult(emptyList())
            }
    }

    // 📤 Enviar solicitud de amistad
    fun enviarSolicitudAmistad(uidDestino: String, usuarioEmisor: Usuario, onResult: (Boolean) -> Unit) {
        val solicitudId = db.child("solicitudes_amistad").child(uidDestino).push().key ?: return

        val solicitud = mapOf(
            "uidEmisor" to usuarioEmisor.uid,
            "nombreEmisor" to usuarioEmisor.nombre,
            "emailEmisor" to usuarioEmisor.email,
            "avatarEmisor" to usuarioEmisor.avatarUrl,
            "timestamp" to ServerValue.TIMESTAMP
        )

        db.child("solicitudes_amistad").child(uidDestino).child(solicitudId)
            .setValue(solicitud)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener {
                Log.e("FriendViewModel", "Error al enviar solicitud: ${it.message}")
                onResult(false)
            }
    }

    // ✅ Aceptar solicitud y agregar amistad bidireccional
    fun aceptarSolicitud(
        uidEmisor: String,
        solicitudId: String,
        datosEmisor: Usuario,
        datosReceptor: Usuario,
        onResult: (Boolean) -> Unit
    ) {
        val updates = mapOf<String, Any?>(
            "usuarios/${datosReceptor.uid}/amigos/$uidEmisor" to datosEmisor,
            "usuarios/$uidEmisor/amigos/${datosReceptor.uid}" to datosReceptor,
            "solicitudes_amistad/${datosReceptor.uid}/$solicitudId" to null
        )

        db.updateChildren(updates)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener {
                Log.e("FriendViewModel", "Error al aceptar solicitud: ${it.message}")
                onResult(false)
            }
    }

    // ❌ Rechazar solicitud (solo elimina)
    fun rechazarSolicitud(uidDestino: String, solicitudId: String, onResult: (Boolean) -> Unit) {
        db.child("solicitudes_amistad").child(uidDestino).child(solicitudId)
            .removeValue()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener {
                Log.e("FriendViewModel", "Error al rechazar solicitud: ${it.message}")
                onResult(false)
            }
    }

    // 📥 Obtener solicitudes recibidas (una sola vez)
    fun obtenerSolicitudesRecibidas(onResult: (List<Pair<String, SolicitudAmistad>>) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.e("FriendViewModel", "Usuario no autenticado")
            onResult(emptyList())
            return
        }

        db.child("solicitudes_amistad").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val solicitudes = snapshot.children.mapNotNull {
                        val solicitud = it.getValue(SolicitudAmistad::class.java)
                        solicitud?.let { s -> it.key?.let { key -> key to s } }
                    }
                    onResult(solicitudes)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FriendViewModel", "Error al obtener solicitudes: ${error.message}")
                    onResult(emptyList())
                }
            })
    }
}
