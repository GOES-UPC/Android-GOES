package com.simplife.skip.models

import java.io.Serializable

class Solicitud (
    var id: Long,
    var mensaje: String,
    var fechaSolicitud: String,
    var horaSolicitud: String,
    var pasajero:Usuario,
    var viaje:Viaje,
    var estadoPasajero: String,
    var parada: Parada,
    var estadoTabla: Boolean
):Serializable{
    override fun toString(): String {
        return "Solicitud(id=$id, mensaje='$mensaje', fechaSolicitud='$fechaSolicitud', horaSolicitud='$horaSolicitud', pasajero=$pasajero, viaje=$viaje, estadoPasajero='$estadoPasajero', parada=$parada, estadoTabla=$estadoTabla)"
    }

}