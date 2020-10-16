package com.simplife.skip.models

import java.io.Serializable

class Rol(

    var id: Long,
    var nombre: String,
    var estadoTabla: Boolean
) : Serializable {
    override fun toString(): String {
        return "Rol(id=$id, nombre='$nombre', estadoTabla=$estadoTabla)"
    }
}