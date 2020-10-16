package com.simplife.skip.models

import java.io.Serializable

class Parada (
    var ubicacion: String,
    var latitud: Double,
    var longitud: Double

) :Serializable {
    override fun toString(): String {
        return "Parada(ubicacion='$ubicacion', latitud=$latitud, longitud=$longitud)"
    }
}