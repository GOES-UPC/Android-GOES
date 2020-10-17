package com.simplife.skip.models
import java.io.Serializable

class ReportRequest (
    var mensaje: String,
    var calificacion: Float
    ): Serializable{
        override fun toString(): String {
            return "SolicitudRequest(mensaje='$mensaje', calificacion=$calificacion)"
        }
}
