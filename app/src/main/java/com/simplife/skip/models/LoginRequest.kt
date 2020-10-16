package com.simplife.skip.models

import java.io.Serializable

class LoginRequest (

    val codigo: String,
    val contrasena: String
) : Serializable {
    override fun toString(): String {
        return "LoginRequest(codigo='$codigo', contrasena='$contrasena')"
    }
}