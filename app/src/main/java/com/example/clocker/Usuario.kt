package com.example.clocker

data class Usuario(
    val id: String,
    var nombreUsuario: String,
    var contrasena: String,
    var rol: String, // "Administrador" o "Reloj"
    var activo: Boolean = true
) {
    fun esAdministrador(): Boolean = rol == "Administrador"
    fun esReloj(): Boolean = rol == "Reloj"
}