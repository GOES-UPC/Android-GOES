package com.simplife.skip.models

import java.io.Serializable

data class PasajeroEnLista (
    var usuarioId: Long,
    var viajeId: Long,
    var nombres: String,
    var imagen: String,
    var estadoPasajero: String,
    var puntoEncuentro: String
): Serializable{

}