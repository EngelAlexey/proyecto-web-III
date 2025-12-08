package com.example.clocker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsuario: TextInputEditText
    private lateinit var etContrasena: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var tvError: TextView
    private lateinit var sessionManager: SessionManager

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Handler para forzar la entrada si Firestore tarda mucho
    private val timeoutHandler = Handler(Looper.getMainLooper())
    private var loginCompletado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            redirigirSegunRol()
            return
        }

        etUsuario = findViewById(R.id.etUsuario)
        etContrasena = findViewById(R.id.etContrasena)
        btnLogin = findViewById(R.id.btnLogin)
        tvError = findViewById(R.id.tvError)

        btnLogin.setOnClickListener {
            tvError.visibility = View.GONE
            val usuarioInput = etUsuario.text.toString().trim()
            val contrasenaInput = etContrasena.text.toString().trim()

            if (usuarioInput.isEmpty() || contrasenaInput.isEmpty()) {
                mostrarError("Por favor complete todos los campos")
            } else {
                btnLogin.isEnabled = false
                btnLogin.text = "Conectando..."
                iniciarProcesoLogin(usuarioInput, contrasenaInput)
            }
        }
    }

    private fun iniciarProcesoLogin(usuario: String, pass: String) {
        db.collection("users")
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    intentarRepararOCrearAdmin(usuario, pass)
                } else {
                    validarCredencialesEnFirestore(usuario, pass)
                }
            }
            .addOnFailureListener { e ->
                // Si falla la lectura inicial, intentamos reparar/crear igualmente por si es la primera vez offline
                intentarRepararOCrearAdmin(usuario, pass)
            }
    }

    private fun intentarRepararOCrearAdmin(usuario: String, pass: String) {
        val emailGenerado = "$usuario@clocker.com"

        auth.signInWithEmailAndPassword(emailGenerado, pass)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: ""
                Toast.makeText(this, "Usuario detectado. Restaurando...", Toast.LENGTH_SHORT).show()
                guardarEnFirestoreConTimeout(uid, usuario, pass, emailGenerado)
            }
            .addOnFailureListener {
                auth.createUserWithEmailAndPassword(emailGenerado, pass)
                    .addOnSuccessListener { authResult ->
                        val uid = authResult.user?.uid ?: ""
                        guardarEnFirestoreConTimeout(uid, usuario, pass, emailGenerado)
                    }
                    .addOnFailureListener { e ->
                        reactivarBoton()
                        if (e is FirebaseAuthUserCollisionException) {
                            mostrarError("Error: El usuario existe en Auth con otra contraseña.")
                        } else {
                            mostrarError("Error Auth: ${e.message}")
                        }
                    }
            }
    }

    private fun guardarEnFirestoreConTimeout(uid: String, usuario: String, pass: String, email: String) {
        val adminMap = hashMapOf(
            "id" to uid,
            "nombreUsuario" to usuario,
            "contrasena" to pass,
            "rol" to "Administrador",
            "activo" to true,
            "email" to email
        )

        loginCompletado = false

        // 1. Iniciamos el guardado real
        db.collection("users").document(uid).set(adminMap)
            .addOnCompleteListener { task ->
                if (!loginCompletado) { // Solo si el timeout no ganó la carrera
                    loginCompletado = true
                    if (task.isSuccessful) {
                        finalizarLogin(uid, usuario, pass, email)
                    } else {
                        // Si falló explícitamente, mostramos error
                        reactivarBoton()
                        mostrarError("Fallo guardado: ${task.exception?.message}")
                    }
                }
            }

        // 2. Iniciamos la "Válvula de Seguridad" (3 segundos)
        timeoutHandler.postDelayed({
            if (!loginCompletado) {
                loginCompletado = true
                Toast.makeText(this, "Conexión lenta. Guardando localmente...", Toast.LENGTH_SHORT).show()
                finalizarLogin(uid, usuario, pass, email)
            }
        }, 3000) // 3000ms = 3 segundos
    }

    private fun finalizarLogin(uid: String, usuario: String, pass: String, email: String) {
        val nuevoAdmin = Usuario(uid, usuario, pass, "Administrador", true, email)
        sessionManager.guardarSesion(nuevoAdmin)

        timeoutHandler.removeCallbacksAndMessages(null)

        redirigirSegunRol()
    }

    private fun validarCredencialesEnFirestore(usuarioInput: String, contrasenaInput: String) {
        db.collection("users")
            .whereEqualTo("nombreUsuario", usuarioInput)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    var loginExitoso = false

                    for (document in documents) {
                        val usuarioEncontrado = document.toObject(Usuario::class.java)
                        val passEnBd = usuarioEncontrado.contrasena?.trim()

                        if (passEnBd == contrasenaInput) {
                            if (usuarioEncontrado.activo) {
                                usuarioEncontrado.id = document.id
                                sessionManager.guardarSesion(usuarioEncontrado)
                                redirigirSegunRol()
                                loginExitoso = true
                                break
                            } else {
                                reactivarBoton()
                                mostrarError("Cuenta desactivada.")
                                loginExitoso = true
                                break
                            }
                        }
                    }
                    if (!loginExitoso) {
                        reactivarBoton()
                        mostrarError("Contraseña incorrecta.")
                    }
                } else {
                    intentarRepararOCrearAdmin(usuarioInput, contrasenaInput)
                }
            }
            .addOnFailureListener { e ->
                reactivarBoton()
                mostrarError("Error conexión: ${e.message}")
            }
    }

    private fun redirigirSegunRol() {
        val intent = if (sessionManager.esAdministrador()) {
            Intent(this, MenuAdministradorActivity::class.java)
        } else {
            Intent(this, ClockActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    private fun mostrarError(mensaje: String) {
        tvError.text = mensaje
        tvError.visibility = View.VISIBLE
    }

    private fun reactivarBoton() {
        btnLogin.isEnabled = true
        btnLogin.text = "Iniciar Sesión"
    }
}