package com.simplife.skip.models

import java.io.Serializable

class Reporte (
    var id: Long,
    var mensaje: String,
    var usuario: Usuario,
    var viaje: Viaje,
    var contenido: String,
    var valoracion: Float,
    var tipoReporte: Boolean,
    var estadoTabla: Boolean
): Serializable {
    override fun toString(): String {
        return "Reporte(id=$id, mensaje='$mensaje', valoracion='$valoracion', estadoTabla=$estadoTabla)"
    }

}