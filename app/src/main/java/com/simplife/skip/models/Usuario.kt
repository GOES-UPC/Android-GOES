package com.simplife.skip.models

import java.io.Serializable

class Usuario (
    var id: Long,
    var dni: String,
    var nombres: String,
    var apellidos: String,
    var sede: String,
    var facebook: String?,
    //var ubicacion: String,
    var imagen: String,
    var estadoTabla: Boolean,
    var cuenta: Cuenta

): Serializable{
    override fun toString(): String {
        return "Usuario(id=$id, dni='$dni', nombres='$nombres', apellidos='$apellidos', sede='$sede', facebook=$facebook, imagen='$imagen', estadoTabla=$estadoTabla, cuenta=$cuenta)"
    }
}