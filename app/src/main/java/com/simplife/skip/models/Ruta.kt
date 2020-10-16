package com.simplife.skip.models

import java.io.Serializable

class Ruta(

    var id: Long,
    var tiempoEstimado: String,
    var sentido: Boolean,
    var distancia: Float,
    var estadoTabla: Boolean


): Serializable {
    override fun toString(): String {
        return "Ruta(id=$id, tiempoEstimado='$tiempoEstimado', sentido=$sentido, distancia=$distancia, estadoTabla=$estadoTabla)"
    }
}