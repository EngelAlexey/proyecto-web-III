package com.example.clocker

import java.security.MessageDigest

object PasswordHelper {

    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun verificarPassword(passwordIngresado: String, passwordHasheado: String): Boolean {
        return hashPassword(passwordIngresado) == passwordHasheado
    }
}