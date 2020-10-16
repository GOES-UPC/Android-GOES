package com.simplife.skip.models

import java.io.Serializable

class Cuenta (
    var id: Long,
    var codigoUpc: String,
    var correoUPC: String,
    var contrasena: String,
    var estadoTabla: Boolean,
    var roles: List<Rol>
) :Serializable {
    override fun toString(): String {
        return "Cuenta(id=$id, codigoUpc='$codigoUpc', correoUPC='$correoUPC', contrasena='$contrasena', estadoTabla=$estadoTabla, roles=$roles)"
    }
}