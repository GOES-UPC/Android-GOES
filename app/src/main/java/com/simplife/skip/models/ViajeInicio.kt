package com.simplife.skip.models

import java.io.Serializable

data class ViajeInicio(
    var id: Long,
    var nombres: String,
    var imagen: String,
    var fechaPublicacion:String,
    var descripcion: String,
    var paradas: List<Parada>,
    var horaInicio: String,
    var horaFin: String,
    var estadoViaje: String
): Serializable{
    constructor(): this(0L, "", "","","", ArrayList<Parada>(),
                        "","","" )
}