package com.simplife.skip.models

import java.io.Serializable

data class Viaje (
    var id: Long,
    var conductor: Usuario,
    var ruta: Ruta,
    var descripcion: String,
    var fechaPublicacion: String,
    var fechaViaje: String,
    var horaInicio: String,
    var horaLlegada: String,
    var horaPublicacion: String,
    var visualizacionHabilitada: Boolean,
    var numeroPasajeros: Int,
    var estadoViaje: String,
    var estadoTabla: Boolean

) : Serializable {
    override fun toString(): String {
        return "Viaje(id=$id, conductor=$conductor, ruta=$ruta, descripcion='$descripcion', fechaPublicacion='$fechaPublicacion', fechaViaje='$fechaViaje', horaInicio='$horaInicio', horaLlegada='$horaLlegada', horaPublicacion='$horaPublicacion', visualizacionHabilitada=$visualizacionHabilitada, numeroPasajeros=$numeroPasajeros, estadoViaje='$estadoViaje', estadoTabla=$estadoTabla)"
    }
}

