// Clase AuthViewModel que extiende de ViewModel
class AuthViewModel : ViewModel() {
    // Instancia de FirebaseAuth para autenticar usuarios
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Variables de estado para manejar errores y estado de inicio de sesión
    var errorMessage by mutableStateOf<String?>(null)
    var isLoggedIn by mutableStateOf(false)

    // Función para iniciar sesión con email y contraseña
    fun login(email: String, password: String) {
        // Utiliza FirebaseAuth para iniciar sesión con email y contraseña
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                // Establece isLoggedIn en true si la tarea es exitosa
                isLoggedIn = task.isSuccessful
                // Si la tarea no es exitosa, establece el mensaje de error
                if (!task.isSuccessful) {
                    errorMessage = task.exception?.message
                }
            }
    }

    // Función para registrar un nuevo usuario con email, contraseña y nombre
    fun register(email: String, password: String, nombre: String, onSuccess: () -> Unit) {
        // Utiliza FirebaseAuth para crear un nuevo usuario con email y contraseña
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                // Si la tarea es exitosa, continúa con el proceso de registro
                if (task.isSuccessful) {
                    // Obtiene el UID del usuario recién creado
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@addOnCompleteListener
                    // Obtiene las iniciales del nombre del usuario
                    val iniciales = obtenerIniciales(nombre)
                    // Genera la URL del avatar del usuario
                    val avatarUrl = generarUrlAvatar(iniciales)

                    // Obtiene el token FCM del dispositivo
                    FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                        // Crea un mapa con los datos del usuario
                        val userData = mapOf(
                            "nombre" to nombre,
                            "email" to email,
                            "avatarUrl" to avatarUrl,
                            "fcmToken" to token // Agrega el token FCM a los datos del usuario
                        )

                        // Guarda los datos del usuario en la base de datos de Firebase Realtime Database
                        FirebaseDatabase.getInstance().getReference("usuarios")
                            .child(uid)
                            .setValue(userData)
                            .addOnSuccessListener {
                                // Loguea un mensaje de éxito al guardar los datos
                                Log.d("Register", "Datos + token guardados")
                                // Llama a la función onSuccess si se guardan los datos correctamente
                                onSuccess()
                            }
                            .addOnFailureListener {
                                // Establece el mensaje de error si falla al guardar los datos
                                errorMessage = "Error al guardar: ${it.message}"
                            }
                    }
                } else {
                    // Establece el mensaje de error si falla al registrar el usuario
                    errorMessage = "Registro fallido: ${task.exception?.message}"
                }
            }
    }

    // Función para cargar los datos del usuario actual
    fun cargarDatosUsuario(onDataLoaded: (String, String, String) -> Unit) {
        // Obtiene el UID del usuario actual
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        // Obtiene la referencia a los datos del usuario en la base de datos
        val ref = FirebaseDatabase.getInstance().getReference("usuarios").child(uid)
        // Lee los datos del usuario desde la base de datos
        ref.get().addOnSuccessListener { snapshot ->
            // Obtiene los datos del usuario desde el snapshot
            val nombre = snapshot.child("nombre").getValue(String::class.java) ?: ""
            val email = snapshot.child("email").getValue(String::class.java) ?: ""
            val avatarUrl = snapshot.child("avatarUrl").getValue(String::class.java) ?: ""
            // Llama a la función onDataLoaded con los datos del usuario
            onDataLoaded(nombre, email, avatarUrl)
        }
    }

    // Función para verificar si hay una sesión activa
    fun verificarSesionActiva(
        onUsuarioActivo: () -> Unit,
        onNoSesion: () -> Unit
    ) {
        // Obtiene el usuario actual
        val usuario = FirebaseAuth.getInstance().currentUser
        // Verifica si el usuario es nulo o no
        if (usuario != null) {
            // Establece isLoggedIn en true si el usuario no es nulo
            isLoggedIn = true
            // Llama a la función onUsuarioActivo si hay una sesión activa
            onUsuarioActivo()
        } else {
            // Establece isLoggedIn en false si el usuario es nulo
            isLoggedIn = false
            // Llama a la función onNoSesion si no hay una sesión activa
            onNoSesion()
        }
    }
}

// Función para generar la URL del avatar del usuario
fun generarUrlAvatar(iniciales: String): String {
    // Retorna una cadena que representa la URL del avatar (en este caso, "bloqueado$iniciales")
    return "bloqueado$iniciales"
}

// Función para obtener las iniciales del nombre del usuario
fun obtenerIniciales(nombreCompleto: String): String {
    // Divide el nombre completo en palabras
    return nombreCompleto
        .split(" ")
        // Filtra las palabras vacías
        .filter { it.isNotBlank() }
        // Obtiene la primera letra de cada palabra y la convierte a mayúsculas
        .map { it.first().uppercaseChar() }
        // Une las letras en una sola cadena
        .joinToString("")
        // Toma solo las dos primeras letras
        .take(2)
}
