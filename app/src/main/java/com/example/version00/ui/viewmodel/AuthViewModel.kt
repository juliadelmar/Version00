package com.example.version00.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var errorMessage by mutableStateOf<String?>(null)
    var isLoggedIn by mutableStateOf(false)

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoggedIn = task.isSuccessful
                if (!task.isSuccessful) {
                    errorMessage = task.exception?.message
                }
            }
    }

    fun register(email: String, password: String, nombre: String, onSuccess: () -> Unit) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@addOnCompleteListener

                    val iniciales = obtenerIniciales(nombre)
                    val avatarUrl = generarUrlAvatar(iniciales)

                    val userData = mapOf(
                        "nombre" to nombre,
                        "email" to email,
                        "avatarUrl" to avatarUrl
                    )

                    FirebaseDatabase.getInstance().getReference("usuarios")
                        .child(uid)
                        .setValue(userData)
                        .addOnSuccessListener {
                            Log.d("Register", "Datos guardados con éxito")
                            onSuccess() // ✅ Navegar a HomeScreen
                        }
                        .addOnFailureListener {
                            errorMessage = "Error al guardar datos: ${it.message}"
                            Log.e("Register", "Error: ${it.message}", it)
                        }

                } else {
                    errorMessage = "Registro fallido: ${task.exception?.message}"
                }
            }
    }
    fun cargarDatosUsuario(onDataLoaded: (String, String, String) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("usuarios").child(uid)
        ref.get().addOnSuccessListener { snapshot ->
            val nombre = snapshot.child("nombre").getValue(String::class.java) ?: ""
            val email = snapshot.child("email").getValue(String::class.java) ?: ""
            val avatarUrl = snapshot.child("avatarUrl").getValue(String::class.java) ?: ""
            onDataLoaded(nombre, email, avatarUrl)
        }
    }
    fun verificarSesionActiva(onUsuarioActivo: () -> Unit) {
        val usuario = FirebaseAuth.getInstance().currentUser
        if (usuario != null) {
            isLoggedIn = true
            onUsuarioActivo()
        }
    }



}
fun generarUrlAvatar(iniciales: String): String {
    return "https://dummyimage.com/200x200/222/ffffff&text=$iniciales"
}

fun obtenerIniciales(nombreCompleto: String): String {
    return nombreCompleto
        .split(" ")
        .filter { it.isNotBlank() }
        .map { it.first().uppercaseChar() }
        .joinToString("")
        .take(2)
}
