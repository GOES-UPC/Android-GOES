package com.simplife.skip.models

import java.io.Serializable

class ViajeRequest (
    var conductorId: Long,
    //True - hacia la universidad
    //False - desde la universidad
    var sentidoRuta: Boolean,
    var partida: Parada,
    var destino: Parada,
    var tiempoEstimado: String,
    var distancia: Float,
    var descripcion: String,
    var fechaViaje: String,
    var horaInicio: String,
    var horaLlegada: String
) : Serializable {
    override fun toString(): String {
        return "ViajeRequest(conductorId=$conductorId, sentidoRuta=$sentidoRuta, partida=$partida, destino=$destino, tiempoEstimado='$tiempoEstimado', distancia=$distancia, descripcion='$descripcion', fechaViaje='$fechaViaje', horaInicio='$horaInicio', horaLlegada='$horaLlegada')"
    }
}