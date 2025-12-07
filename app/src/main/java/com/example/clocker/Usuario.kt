package com.example.clocker

data class Usuario(
    var id: String = "",
    var nombreUsuario: String = "",
    var contrasena: String? = null,
    var rol: String = "",
    var activo: Boolean = true,
    var email: String = ""
) {
    constructor() : this("", "", null, "", true, "")

    val esAdministrador: Boolean
        get() = rol == "Administrador"

    val esReloj: Boolean
        get() = rol == "Reloj"
}